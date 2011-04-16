package de.heiden.c64dt.assembler;

import de.heiden.c64dt.assembler.command.AddressCommand;
import de.heiden.c64dt.assembler.command.BitCommand;
import de.heiden.c64dt.assembler.command.CommandBuffer;
import de.heiden.c64dt.assembler.command.DataCommand;
import de.heiden.c64dt.assembler.command.DummyCommand;
import de.heiden.c64dt.assembler.command.ICommand;
import de.heiden.c64dt.assembler.command.OpcodeCommand;
import de.heiden.c64dt.assembler.detector.BitDetector;
import de.heiden.c64dt.assembler.detector.BrkDetector;
import de.heiden.c64dt.assembler.detector.IDetector;
import de.heiden.c64dt.assembler.detector.Jsr0Detector;
import de.heiden.c64dt.assembler.detector.LabelDetector;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import static de.heiden.c64dt.util.ByteUtil.toWord;
import static de.heiden.c64dt.util.HexUtil.hex;
import static de.heiden.c64dt.util.HexUtil.hexBytePlain;
import static de.heiden.c64dt.util.HexUtil.hexWord;
import static de.heiden.c64dt.util.HexUtil.hexWordPlain;

/**
 * Reassembler.
 */
public class Reassembler
{
  private final List<IDetector> detectors = new ArrayList<IDetector>();

  /**
   * Constructor.
   */
  public Reassembler() {
    // add default detectors
    detectors.add(new LabelDetector());
    detectors.add(new BrkDetector());
    detectors.add(new BitDetector());
    detectors.add(new Jsr0Detector());
  }

  /**
   * Add code type detector to use.
   *
   * @param detector code type detector
   */
  public void add(IDetector detector) {
    Assert.notNull(detector, "Precondition: detector != null");

    detectors.add(detector);
  }

  /**
   * Reassemble program.
   *
   * @param input program with start address
   * @param output output for reassembled code
   */
  public void reassemble(InputStream input, Writer output) throws IOException
  {
    Assert.notNull(input, "Precondition: input != null");
    Assert.notNull(output, "Precondition: output != null");

    reassemble(FileCopyUtils.copyToByteArray(input), output);
  }

  /**
   * Reassemble program.
   *
   * @param program program with start address
   * @param output output for reassembled code
   */
  public void reassemble(byte[] program, Writer output) throws IOException
  {
    Assert.notNull(program, "Precondition: program != null");
    Assert.isTrue(program.length >= 2, "Precondition: program.length >= 2");
    Assert.notNull(output, "Precondition: output != null");

    int address = toWord(program, 0);
    byte[] code = new byte[program.length - 2];
    System.arraycopy(program, 2, code, 0, code.length);
    reassemble(address, code, output);
  }

  /**
   * Reassemble.
   *
   * @param startAddress start address of code
   * @param code program without start address
   * @param output output for reassembled code
   */
  public void reassemble(int startAddress, InputStream code, Writer output) throws IOException
  {
    Assert.notNull(code, "Precondition: input != null");
    Assert.notNull(output, "Precondition: output != null");

    reassemble(startAddress, FileCopyUtils.copyToByteArray(code), output);
  }

  /**
   * Reassemble.
   *
   * @param startAddress start address of code
   * @param code program without start address
   * @param output output for reassembled code
   */
  public void reassemble(int startAddress, byte[] code, Writer output) throws IOException
  {
    Assert.isTrue(startAddress >= 0, "Precondition: startAddress >= 0");
    Assert.notNull(code, "Precondition: code != null");
    Assert.notNull(output, "Precondition: output != null");


    if (startAddress == 0x0801)
    {
      // TODO check for basic header
    }

    reassemble(code, new CommandBuffer(code, startAddress), output);
  }

  /**
   * Reassemble.
   *
   * @param code program without start address
   * @param commands command buffer
   * @param output output for reassembled code
   */
  public void reassemble(byte[] code, CommandBuffer commands, Writer output) throws IOException
  {
    Assert.notNull(code, "Precondition: code != null");
    Assert.notNull(output, "Precondition: output != null");

    CodeBuffer buffer = new CodeBuffer(code);

    boolean change = true;
    for (int count = 0; change && count < 10; count++)
    {
      tokenize(buffer, commands);
      reachability(commands);
      change = detectCodeType(commands);
      System.out.println(count);
    }

    combine(commands);
    write(commands, new BufferedWriter(output, code.length * 80));
  }

  /**
   * Tokenize code.
   *
   * @param code code buffer
   * @param commands command buffer
   */
  private void tokenize(CodeBuffer code, CommandBuffer commands)
  {
    Assert.notNull(code, "Precondition: code != null");

    code.restart();
    commands.clear();
    while (code.has(1))
    {
      int index = commands.getCurrentIndex();
      int pc = commands.addressForIndex(index);
      CodeType type = commands.getType(index);
      if (type == CodeType.ABSOLUTE_ADDRESS)
      {
        // absolute address reference as data
        int address = code.read(2);
        commands.addCommand(new AddressCommand(address));
        commands.addCodeReference(index, address);

      }
      else if (type.isData())
      {
        // plain data
        commands.addCommand(new DataCommand(code.readByte()));
      }
      else
      {
        // unknown or code -> try to disassemble an opcode
        Opcode opcode = code.readOpcode();
        OpcodeMode mode = opcode.getMode();
        int size = mode.getSize();

        if (code.has(1 + size))
        {
          if (opcode.isLegal() || type == CodeType.OPCODE)
          {
            // TODO log error if illegal opcode and type is OPCODE?
            if (size == 0)
            {
              // opcode without argument
              commands.addCommand(new OpcodeCommand(opcode));
            }
            else
            {
              // opcode with an argument
              int argument = code.read(mode.getSize());
              commands.addCommand(new OpcodeCommand(opcode, argument));
              if (mode.isAddress())
              {
                int address = mode.getAddress(pc, argument);
                // track references of opcodes
                commands.addReference(opcode.getType().isJump(), index, address);
              }
            }
          }
          else
          {
            // no valid opcode -> assume data
            commands.addCommand(new DataCommand(opcode.getOpcode()));
          }
        }
        else
        {
          // not enough argument bytes for opcode -> assume data
          // TODO log error, when type != UNKNOWN?
          commands.addCommand(new DataCommand(opcode.getOpcode()));
        }
      }
    }
  }

  /**
   * Compute transitive unreachability of commands.
   *
   * @param commands command buffer
   */
  private void reachability(CommandBuffer commands)
  {
    boolean change;
    do
    {
      commands.restart();
      change = false;

      // tracing forward from one unreachable command to the next
      ICommand lastCommand = new DummyCommand();
      while (commands.hasNextCommand())
      {
        ICommand command = commands.nextCommand();
        /*
         * A command is not reachable, if the previous command is not reachable or is an ending command (e.g. JMP) and
         * there is no code label for the command and the command has not already been detected as an opcode.
         */
        if (command.isReachable() && !commands.hasCodeLabel() && commands.getType() != CodeType.OPCODE &&
          (!lastCommand.isReachable() || lastCommand.isEnd()))
        {
          command.setReachable(false);
          // restart, if reference caused a wrong label in the already scanned code
          change |= commands.removeReference();
        }

        lastCommand = command;
      }

      // trace backward from unreachable command to the previous
      lastCommand = new DummyCommand();
      while (commands.hasPreviousCommand())
      {
        ICommand command = commands.previousCommand();
        /*
         * A code command is not reachable, if it leads to unreachable code.
         * Exception: JSR which may be followed by argument data.
         */
        if (!lastCommand.isReachable() &&
          command.isReachable() && !isJsr(command) && !command.isEnd() && commands.getType() != CodeType.OPCODE)
        {
          command.setReachable(false);
          // restart, if reference caused a wrong label in the already scanned code
          change |= commands.removeReference();
          // TODO mh: Change code type to data?
        }

        lastCommand = command;
      }
    } while (change);
  }

  private boolean isJsr(ICommand command)
  {
    return command instanceof OpcodeCommand && ((OpcodeCommand) command).getOpcode().getType() == OpcodeType.JSR;
  }

  /**
   * Detect type of code.
   *
   * @param commands command buffer
   * @return whether a code label has been altered
   */
  private boolean detectCodeType(CommandBuffer commands)
  {
    boolean result = false;

    for (IDetector detector : detectors)
    {
      result |= detector.detect(commands);
    }

    // TODO iseahe: setType() for label locations?
    // TODO iseahe: setType(OPCODE/CODE) for OpcodeCommands?
    // TODO iseahe: detect self modifying code
    // TODO iseahe: detect JSR with parameters afterwards

    return result;
  }

  /**
   * Combine commands, if possible.
   *
   * @param commands command buffer
   */
  private void combine(CommandBuffer commands)
  {
    Assert.notNull(commands, "Precondition: buffer != null");

    commands.restart();

    ICommand lastCommand = null;
    while (commands.hasNextCommand())
    {
      ICommand command = commands.nextCommand();
      if (!commands.hasLabel() && lastCommand != null && lastCommand.combineWith(command))
      {
        // TODO let command buffer handle this functionality?
        commands.removeCurrentCommand();
      }
      else
      {
        lastCommand = command;
      }
    }
  }

  /**
   * Write commands to output writer.
   *
   * @param commands command buffer
   * @param output writer to write output to
   */
  private void write(CommandBuffer commands, Writer output) throws IOException
  {
    Assert.notNull(commands, "Precondition: buffer != null");
    Assert.notNull(output, "Precondition: output != null");

    commands.restart();

    // start address
    output.append("*=").append(hexWord(commands.getStartAddress())).append("\n");
    output.append("\n");

    // external labels
    Collection<ExternalLabel> externalReferences = new TreeSet<ExternalLabel>(commands.getExternalLabels());
    for (ExternalLabel externalReference : externalReferences)
    {
      output.append(externalReference.toString()).append(" = ").append(hex(externalReference.getAddress())).append("\n");
    }
    output.append("\n");

    // code
    StringBuilder line = new StringBuilder(80);
    while (commands.hasNextCommand())
    {
      ICommand command = commands.nextCommand();
      int pc = command.getAddress();

      line.setLength(0);

      // debug output: prefixes
      if (command == null)
      {
        line.append("?");
      }
      else
      {
        if (!command.isReachable())
        {
          line.append("U");
        }
        for (int i = 1; i < command.getSize(); i++)
        {
          if (commands.hasCodeLabel(pc + i))
          {
            line.append("C");
          }
          if (commands.hasDataLabel(pc + i))
          {
            line.append("D");
          }
        }
      }
      fillSpaces(line, 5);
      line.append(" | ");

      // debug output: byte representation of command
      line.append(hexWordPlain(pc));
      List<Integer> data = command.toBytes();
      for (int i = 0; i < data.size() && i < 3; i++)
      {
        line.append(" ");
        line.append(hexBytePlain(data.get(i)));
      }
      fillSpaces(line, 21);
      line.append(data.size() > 3 ? "..." : "   ");
      line.append(" | ");

      // reassembler output
      ILabel label = commands.getLabel();
      if (label != null)
      {
        // TODO mh: check length of label?
        line.append(label.toString()).append(":");
      }

      fillSpaces(line, 40);
      output.write(line.toString());

      if (command != null)
      {
        output.append(command.toString(commands));
      }
      else
      {
        // TODO mh: log error?
        output.append("???");
      }

      output.append("\n");
    }

    output.flush();
  }

  /**
   * Fill line with spaces up to a limit.
   *
   * @param line line
   * @param num limit
   */
  private void fillSpaces(StringBuilder line, int num)
  {
    while (line.length() < num)
    {
      line.append(' ');
    }
  }
}

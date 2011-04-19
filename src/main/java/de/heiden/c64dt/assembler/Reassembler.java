package de.heiden.c64dt.assembler;

import de.heiden.c64dt.assembler.command.AddressCommand;
import de.heiden.c64dt.assembler.command.CommandBuffer;
import de.heiden.c64dt.assembler.command.DataCommand;
import de.heiden.c64dt.assembler.command.DummyCommand;
import de.heiden.c64dt.assembler.command.ICommand;
import de.heiden.c64dt.assembler.command.OpcodeCommand;
import de.heiden.c64dt.assembler.detector.BitDetector;
import de.heiden.c64dt.assembler.detector.BrkDetector;
import de.heiden.c64dt.assembler.detector.IDetector;
import de.heiden.c64dt.assembler.detector.JsrDetector;
import de.heiden.c64dt.assembler.detector.LabelDetector;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

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
  private CommandBuffer commands;
  private final List<IDetector> detectors = new ArrayList<IDetector>();

  /**
   * Constructor.
   */
  public Reassembler() {
    // add default detectors
    detectors.add(new LabelDetector());
    detectors.add(new BrkDetector());
    detectors.add(new BitDetector());
    detectors.add(new JsrDetector());
  }

  /**
   * Command buffer.
   */
  public CommandBuffer getCommands() {
    return commands;
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
   * All detectors.
   * Just for the mapper.
   */
  List<IDetector> getDetectors() {
    return detectors;
  }

  /**
   * Reassemble program.
   *
   * @param input program with start address
   */
  public void reassemble(InputStream input) throws IOException
  {
    Assert.notNull(input, "Precondition: input != null");

    reassemble(FileCopyUtils.copyToByteArray(input));
  }

  /**
   * Reassemble program.
   *
   * @param program program with start address
   */
  public void reassemble(byte[] program) throws IOException
  {
    Assert.notNull(program, "Precondition: program != null");
    Assert.isTrue(program.length >= 2, "Precondition: program.length >= 2");

    int address = toWord(program, 0);
    byte[] code = new byte[program.length - 2];
    System.arraycopy(program, 2, code, 0, code.length);
    reassemble(address, code);
  }

  /**
   * Reassemble.
   *
   * @param startAddress start address of code
   * @param code program without start address
   */
  public void reassemble(int startAddress, InputStream code) throws IOException
  {
    Assert.notNull(code, "Precondition: input != null");

    reassemble(startAddress, FileCopyUtils.copyToByteArray(code));
  }

  /**
   * Reassemble.
   *
   * @param startAddress start address of code
   * @param code program without start address
   */
  public void reassemble(int startAddress, byte[] code) throws IOException
  {
    Assert.isTrue(startAddress >= 0, "Precondition: startAddress >= 0");
    Assert.notNull(code, "Precondition: code != null");

    if (startAddress == 0x0801)
    {
      // TODO check for basic header
    }

    reassemble(new CommandBuffer(code, startAddress));
  }

  /**
   * Reassemble.
   *
   * @param commands command buffer
   */
  public void reassemble(CommandBuffer commands) throws IOException
  {
    Assert.notNull(commands, "Precondition: commands != null");

    this.commands = commands;

    CodeBuffer buffer = new CodeBuffer(commands.getCode());

    boolean change = true;
    for (int count = 0; change && count < 10; count++)
    {
      tokenize(buffer);
      reachability();
      change = detectCodeType();
      System.out.println(count);
    }

    combine();
  }

  /**
   * Tokenize code.
   *
   * @param code code buffer
   */
  private void tokenize(CodeBuffer code)
  {
    Assert.notNull(code, "Precondition: code != null");

    code.restart();
    commands.clear();
    while (code.has(1))
    {
      int index = commands.geNextIndex();
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
   */
  private void reachability()
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

  /**
   * Is command a JSR opcode?.
   *
   * @param command command
   */
  private boolean isJsr(ICommand command)
  {
    return command instanceof OpcodeCommand && ((OpcodeCommand) command).getOpcode().getType() == OpcodeType.JSR;
  }

  /**
   * Detect type of code.
   *
   * @return whether a code label has been altered
   */
  private boolean detectCodeType()
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
   */
  private void combine()
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
   * @param output writer to write output to
   */
  public void write(Writer output) throws IOException
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

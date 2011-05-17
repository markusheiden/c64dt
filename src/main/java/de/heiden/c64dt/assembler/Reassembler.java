package de.heiden.c64dt.assembler;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import de.heiden.c64dt.assembler.command.CommandBuffer;
import de.heiden.c64dt.assembler.command.CommandIterator;
import de.heiden.c64dt.assembler.command.ICommand;
import de.heiden.c64dt.assembler.detector.BitDetector;
import de.heiden.c64dt.assembler.detector.BrkDetector;
import de.heiden.c64dt.assembler.detector.IDetector;
import de.heiden.c64dt.assembler.detector.JsrDetector;
import de.heiden.c64dt.assembler.detector.LabelDetector;
import de.heiden.c64dt.assembler.detector.Reachability;
import de.heiden.c64dt.assembler.detector.Tokenizer;
import de.heiden.c64dt.assembler.label.ExternalLabel;
import de.heiden.c64dt.assembler.label.ILabel;
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
@XStreamAlias("reassembler")
public class Reassembler
{
  private CommandBuffer commands;
  private final List<IDetector> detectors = new ArrayList<IDetector>();

  /**
   * Constructor.
   */
  public Reassembler()
  {
    // add default detectors
    detectors.add(new Tokenizer());
    detectors.add(new Reachability());
    detectors.add(new LabelDetector());
    detectors.add(new BrkDetector());
    detectors.add(new BitDetector());
    detectors.add(new JsrDetector());
  }

  /**
   * Command buffer.
   */
  public CommandBuffer getCommands()
  {
    return commands;
  }

  /**
   * Add code type detector to use.
   *
   * @param detector code type detector
   */
  public void add(IDetector detector)
  {
    Assert.notNull(detector, "Precondition: detector != null");

    detectors.add(detector);
  }

  /**
   * All detectors.
   * Just for the mapper.
   */
  List<IDetector> getDetectors()
  {
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
   * Reassemble again.
   */
  public void reassemble()
  {
    reassemble(commands);
  }

  /**
   * Reassemble.
   *
   * @param commands command buffer
   */
  public void reassemble(CommandBuffer commands)
  {
    Assert.notNull(commands, "Precondition: commands != null");

    this.commands = commands;

    boolean change = true;
    for (int count = 0; change && count < 10; count++)
    {
      change = detectCodeType();
      System.out.println(count);
    }

    combine();
  }

  /**
   * Detect type of code.
   *
   * @return whether a code label has been altered
   */
  private boolean detectCodeType()
  {
    boolean change = false;

    for (IDetector detector : detectors)
    {
      boolean detectorHit = detector.detect(commands);
      if (detectorHit)
      {
        System.out.println(detector.getClass().getSimpleName());
      }
      change |= detectorHit;
    }

    // TODO iseahe: setType() for label locations?
    // TODO iseahe: setType(OPCODE/CODE) for OpcodeCommands?
    // TODO iseahe: detect self modifying code
    // TODO iseahe: detect JSR with parameters afterwards

    return change;
  }

  /**
   * Combine commands, if possible.
   */
  private void combine()
  {
    Assert.notNull(commands, "Precondition: buffer != null");

    CommandIterator iter = new CommandIterator(commands);

    ICommand lastCommand = null;
    while (iter.hasNextCommand())
    {
      ICommand command = iter.nextCommand();
      if (!iter.hasLabel() && lastCommand != null && lastCommand.combineWith(command))
      {
        // TODO let command buffer handle this functionality?
        iter.removeCommand();
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

    CommandIterator iter = new CommandIterator(commands);

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
    while (iter.hasNextCommand())
    {
      ICommand command = iter.nextCommand();
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
      line.append(data.size() > 3? "..." : "   ");
      line.append(" | ");

      // reassembler output
      ILabel label = iter.getLabel();
      if (label != null)
      {
        // TODO mh: check length of label?
        line.append(label.toString(pc)).append(":");
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

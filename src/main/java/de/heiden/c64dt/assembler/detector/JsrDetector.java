package de.heiden.c64dt.assembler.detector;

import de.heiden.c64dt.assembler.CodeBuffer;
import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.assembler.Opcode;
import de.heiden.c64dt.assembler.OpcodeMode;
import de.heiden.c64dt.assembler.OpcodeType;
import de.heiden.c64dt.assembler.command.CommandBuffer;
import de.heiden.c64dt.assembler.command.CommandIterator;
import de.heiden.c64dt.assembler.command.ICommand;
import de.heiden.c64dt.assembler.command.OpcodeCommand;
import de.heiden.c64dt.assembler.command.Subroutine;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import javax.xml.bind.annotation.XmlAttribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static de.heiden.c64dt.util.HexUtil.hexWord;

/**
 * Detects JSR commands to predefined address which are followed by fixed length or zero-terminated arguments.
 * Additionally it automatically detects JSR commands which are followed by their zero-terminated arguments.
 */
public class JsrDetector implements IDetector
{
  private final Logger logger = Logger.getLogger(getClass());

  /**
   * Minimum number of matches (check for expected argument type) to detect a new subroutine.
   */
  @XmlAttribute(name = "min-matches")
  private int minMatches = 2;

  /**
   * Minimum ratio of unreachable code after JSR to detect a new subroutine.
   */
  @XmlAttribute(name = "unreachable-ration")
  private double unreachableRatio = 0.2;

  /**
   * Minimum ratio of matches (check for expected argument type) to detect a new subroutine.
   */
  @XmlAttribute(name = "match-ration")
  private double matchRatio = 0.8;

  /**
   * Maximum length of zero-terminated argument after JSR opcode.
   */
  @XmlAttribute(name = "max-length")
  private int maxLength = 256;

  @Override
  public boolean detect(CommandBuffer commands)
  {
    boolean change = false;

    Map<Integer, List<Integer>> crossReferences = crossReference(commands);
    for (Entry<Integer, List<Integer>> crossReference : crossReferences.entrySet())
    {
      int address = crossReference.getKey();
      List<Integer> references = crossReference.getValue();

      Subroutine subroutine = commands.getSubroutine(address);
      if (subroutine == null)
      {
        subroutine = detectZero(commands, address, references);
      }
      if (subroutine == null)
      {
        subroutine = detectAddress(commands, address, references);
      }
      if (subroutine == null)
      {
        continue;
      }

      // mark subroutine calls
      for (int index : references)
      {
        if (subroutine.getArguments() == 0)
        {
          logger.debug("Known subroutine with zero terminated argument at address " + hexWord(address) + ", referenced at index " + hexWord(index));

          // search the zero which is terminating the argument
          int endIndex = search0(commands, index + 1, false);
          if (endIndex < 0)
          {
            continue;
          }

          change |= markJSR(commands, index, endIndex, subroutine.getType());
        }
        else if (subroutine.getArguments() > 0)
        {
          logger.debug("Known subroutine with " + subroutine.getArguments() + " byte argument at address " + hexWord(address) + ", referenced at index " + hexWord(index));

          // fixed length argument
          change |= markJSR(commands, index, index + 3 + subroutine.getArguments(), subroutine.getType());
        }
        // else: "normal" subroutine
      }
    }

    return change;
  }

  /**
   * Build JSR cross reference.
   *
   * @param commands command buffer
   * @return Absolute address to list of relative addresses of JSR to that absolute address
   */
  protected Map<Integer, List<Integer>> crossReference(CommandBuffer commands)
  {
    Map<Integer, List<Integer>> result = new HashMap<Integer, List<Integer>>();

    for (CommandIterator iter = new CommandIterator(commands); iter.hasNextCommand(); )
    {
      ICommand command = iter.nextCommand();
      if (!(command instanceof OpcodeCommand))
      {
        continue;
      }
      OpcodeCommand opcodeCommand = (OpcodeCommand) command;

      Opcode opcode = opcodeCommand.getOpcode();
      if (command.isReachable() && opcode.getType().equals(OpcodeType.JSR) && opcode.getMode().equals(OpcodeMode.ABS) && iter.hasNextCommand())
      {
        int address = opcodeCommand.getArgument();
        List<Integer> references = result.get(address);
        if (references == null)
        {
          references = new ArrayList<Integer>();
          result.put(address, references);
        }
        references.add(iter.getIndex());
      }
    }

    return result;
  }

  /**
   * Detect yet unknown subroutines with zero terminated argument.
   *
   * @param commands command buffer
   * @param address address of the subroutine
   * @param references all references to that subroutine
   * @return Subroutine or null, if no consistent type of subroutine could be detected
   */
  protected Subroutine detectZero(CommandBuffer commands, int address, List<Integer> references)
  {
    int matches = 0;
    int unreachable = 0;
    int count = 0;
    for (int index : references)
    {
      // try automatic detection of zero-terminated argument
      int endIndex = search0(commands, index + 1, true);
      if (endIndex >= 0)
      {
        matches++;
        if (!commands.getCommand(index + 3).isReachable())
        {
          unreachable++;
        }
      }
      count++;
    }

    return createSubroutine(commands, "zero terminated argument", address, 0, CodeType.DATA, matches, unreachable, count);
  }

  /**
   * Detect yet unknown subroutines with absolute address argument.
   *
   * @param commands command buffer
   * @param address address of the subroutine
   * @param references all references to that subroutine
   * @return Subroutine or null, if no consistent type of subroutine could be detected
   */
  protected Subroutine detectAddress(CommandBuffer commands, int address, List<Integer> references)
  {
    CodeBuffer buffer = new CodeBuffer(commands.getCode());

    int matches = 0;
    int unreachable = 0;
    int count = 0;
    for (int index : references)
    {
      buffer.setCurrentIndex(index);
      if (buffer.has(3 + 2))
      {
        buffer.setCurrentIndex(index + 3);
        if (commands.hasAddress(buffer.read(2)))
        {
          matches++;
          if (!commands.getCommand(index + 3).isReachable())
          {
            unreachable++;
          }
        }
      }
      count++;
    }

    // set unreachable to number of matches, to disable unreachableRation
    return createSubroutine(commands, "absolute address argument", address, 2, CodeType.ABSOLUTE_ADDRESS, matches, matches, count);
  }

  private Subroutine createSubroutine(CommandBuffer commands, String kind, int address, int arguments, CodeType type, int matches, int unreachable, int count)
  {
    if (matches == 0)
    {
      // nothing detected
      return null;
    }

    double detectedUnreachableRatio = ((double) unreachable) / ((double) count);
    double detectedMatchRatio = ((double) matches) / ((double) count);
    if (matches < minMatches || detectedUnreachableRatio < unreachableRatio || detectedMatchRatio < matchRatio)
    {
      logger.debug("Potential subroutine with " + kind + " at address " + hexWord(address) + ": Probability " + matches + " (" + unreachable + ") / " + count);
      return null;
    }

    logger.debug("Detected subroutine with " + kind + " at address " + hexWord(address) + ": Probability " + matches + " (" + unreachable + ") / " + count);
    Subroutine result = new Subroutine(address, arguments, type);
    commands.addSubroutine(result);

    return result;
  }

  /**
   * Search end of zero-terminated argument.
   * Stops after 256 Bytes.
   *
   * @param commands command buffer
   * @param startIndex index to start at
   * @param check stop at labels?
   * @return end index or -1, if no arguments have been found
   */
  private int search0(CommandBuffer commands, int startIndex, boolean check)
  {
    Assert.notNull(commands, "Precondition: commands != null");

    byte[] code = commands.getCode();
    for (int index = startIndex, count = 0; commands.hasIndex(index) && count < maxLength; index++)
    {
      // check for aborting conditions, only if requested
      if (check)
      {
        if (commands.hasLabel(commands.addressForIndex(index)))
        {
          // stop search at any label
          return -1;
        }
        CodeType type = commands.getType(index);
        if (!type.isUnknown() && !type.isData())
        {
          // stop if code type has already been determined
          return -1;
        }
      }

      if (code[index] == 0)
      {
        // terminating zero found, return index of following code
        return index + 1;
      }
    }

    // no valid zero-terminated argument found
    return -1;
  }

  /**
   * Markus argument as data and following opcode as code.
   *
   * @param commands command buffer
   * @param index index of JSR
   * @param endIndex end index of data (excl.), next opcode
   * @param type Code type of argument
   * @return whether a change has taken place
   */
  private boolean markJSR(CommandBuffer commands, int index, int endIndex, CodeType type)
  {
    if (!commands.hasIndex(endIndex))
    {
      return false;
    }

    // Add reference to make code after the argument reachable
    commands.addCodeReference(index, commands.addressForIndex(endIndex));

    return
      // Mark JSR as opcode
      commands.setType(index, CodeType.OPCODE) |
        // Mark argument bytes as data
        commands.setType(index + 1, endIndex, type) |
        // At endIndex the code continues
        commands.setType(endIndex, CodeType.OPCODE);
  }
}

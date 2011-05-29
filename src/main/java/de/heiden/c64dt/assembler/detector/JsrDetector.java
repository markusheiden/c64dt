package de.heiden.c64dt.assembler.detector;

import de.heiden.c64dt.assembler.CodeType;
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
   * Maximum length of zero-terminated argument after JSR opcode.
   * <p/>
   * TODO mh: make configurable?
   */
  @XmlAttribute(name = "max")
  private final int maxLength = 256;

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

      for (int reference : references)
      {
        if (subroutine == null)
        {
          // try automatic detection of zero-terminated argument
          int endIndex = search0(commands, reference + 1, true);
          if (endIndex < 0)
          {
            continue;
          }

          logger.debug("Detected subroutine with zero terminated argument at address " + hexWord(address) + ", referenced at index " + hexWord(reference));
          change |= markArgument(commands, reference, reference + 1, endIndex, CodeType.DATA);
        }
        else if (subroutine.getArguments() == 0)
        {
          logger.debug("Known subroutine with zero terminated argument at address " + hexWord(address) + ", referenced at index " + hexWord(reference));

          // search the zero which is terminating the argument
          int endIndex = search0(commands, reference + 1, false);
          if (endIndex < 0)
          {
            continue;
          }

          change |= markArgument(commands, reference, reference + 1, endIndex, subroutine.getType());
        }
        else if (subroutine.getArguments() > 0)
        {
          logger.debug("Known subroutine with " + subroutine.getArguments() + " byte argument at address " + hexWord(address) + ", referenced at index " + hexWord(reference));

          // fixed length argument
          change |= markArgument(commands, reference, reference + 1, reference + 1 + subroutine.getArguments(), subroutine.getType());
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

      if (opcodeCommand.getOpcode().getType().equals(OpcodeType.JSR) &&
        opcodeCommand.getOpcode().getMode().equals(OpcodeMode.ABS) &&
        iter.hasNextCommand())
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
   * Markus argument as data and following opcode as code.
   *
   * @param commands command buffer
   * @param index index of JSR
   * @param startIndex index of data, argument
   * @param endIndex end index of data (excl.), next opcode
   * @param type Code type of argument
   * @return whether a change has taken place
   */
  private boolean markArgument(CommandBuffer commands, int index, int startIndex, int endIndex, CodeType type)
  {
    if (!commands.hasIndex(endIndex))
    {
      return false;
    }

    // Add reference to make code after the argument reachable
    // TODO mh: read absolute address from jsr?
    commands.addCodeReference(index, commands.addressForIndex(endIndex));

    return
      // Mark argument bytes as data
      commands.setType(startIndex, endIndex, type) |
        // At endIndex the code continues
        commands.setType(endIndex, CodeType.OPCODE);
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
}

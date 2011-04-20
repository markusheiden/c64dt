package de.heiden.c64dt.assembler.detector;

import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.assembler.OpcodeMode;
import de.heiden.c64dt.assembler.OpcodeType;
import de.heiden.c64dt.assembler.command.CommandBuffer;
import de.heiden.c64dt.assembler.command.CommandIterator;
import de.heiden.c64dt.assembler.command.ICommand;
import de.heiden.c64dt.assembler.command.OpcodeCommand;
import org.springframework.util.Assert;

/**
 * Detects JSR commands to predefined address which are followed by fixed length or zero-terminated arguments.
 * Additionally it automatically detects JSR commands which are followed by their zero-terminated arguments.
 */
public class JsrDetector implements IDetector
{
  /**
   * Maximum length of zero-terminated argument after JSR opcode.
   *
   * TODO mh: make configurable?
   */
  private final int maxLength = 256;

  @Override
  public boolean detect(CommandBuffer commands)
  {
    boolean change = false;

    for (CommandIterator iter = new CommandIterator(commands); iter.hasNextCommand();)
    {
      ICommand command = iter.nextCommand();
      int index = iter.getIndex();
      if (command instanceof OpcodeCommand)
      {
        OpcodeCommand opcodeCommand = (OpcodeCommand) command;
        if (!opcodeCommand.getOpcode().getType().equals(OpcodeType.JSR) ||
            !opcodeCommand.getOpcode().getMode().equals(OpcodeMode.ABS) ||
            !iter.hasNextCommand()) {
          // no JSR $xxxx or no arguments
          continue;
        }

        int argumentsIndex = iter.getNextIndex();
        int arguments = commands.getSubroutineArguments(opcodeCommand.getArgument());
        if (arguments > 0)
        {
          change |= markArgument(commands, index, argumentsIndex, argumentsIndex + arguments);
        }
        else if (arguments == 0 || !iter.peekCommand().isReachable())
        {
          // argument == 0: manual defined JSR with zero-terminated argument
          // !commands.peekCommand().isReachable(): try automatic detection of zero-terminated argument
          int endIndex = search0(commands, argumentsIndex, arguments != 0);
          if (endIndex < 0) {
            continue;
          }

          change |= markArgument(commands, index, argumentsIndex, endIndex);
        }
      }
    }

    return change;
  }

  /**
   * Markus argument as data and following opcode as code.
   *
   * @param commands command buffer
   * @param index index of JSR
   * @param startIndex index of data, argument
   * @param endIndex end index of data (excl.), next opcode
   * @return whether a change has taken place
   */
  private boolean markArgument(CommandBuffer commands, int index, int startIndex, int endIndex) {
    if (!commands.hasIndex(endIndex)) {
      return false;
    }

    // Add reference to make code after the argument reachable
    commands.addCodeReference(index, commands.addressForIndex(endIndex));

    return
      // Mark argument bytes as data
      commands.setType(startIndex, endIndex, CodeType.DATA) |
      // At endIndex the code continues
      commands.setType(endIndex, CodeType.OPCODE);
  }

  /**
   * Search end of zero-terminated argument.
   * Stops after 256 Bytes.
   *
   * @param commands command buffer
   * @param startIndex index to start at
   * @param stopAtLabels search at labels?
   * @return end index or -1, if no arguments have been found
   */
  private int search0(CommandBuffer commands, int startIndex, boolean stopAtLabels) {
    Assert.notNull(commands, "Precondition: commands != null");

    byte[] code = commands.getCode();
    for (int index = startIndex, count = 0; commands.hasIndex(index) && count < maxLength; index++)
    {
      if (stopAtLabels && commands.hasLabel(commands.addressForIndex(index))) {
        // stop search at any label
        return -1;
      }

      if (code[index] == 0) {
        // terminating zero found, return index of following code
        return index + 1;
      }
    }

    // no valid zero-terminated argument found
    return -1;
  }
}

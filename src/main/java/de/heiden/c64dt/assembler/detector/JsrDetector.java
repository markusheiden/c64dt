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

    CommandIterator iter = new CommandIterator(commands);

    while (iter.hasNextCommand())
    {
      ICommand command = iter.nextCommand();
      int index = iter.getCurrentIndex();
      if (command instanceof OpcodeCommand)
      {
        OpcodeCommand opcodeCommand = (OpcodeCommand) command;
        if (!opcodeCommand.getOpcode().getType().equals(OpcodeType.JSR) ||
            !opcodeCommand.getOpcode().getMode().equals(OpcodeMode.ABS) ||
            !iter.hasNextCommand()) {
          // no JSR $xxxx or no arguments
          continue;
        }

        int arguments = commands.getSubroutineArguments(opcodeCommand.getArgument());
        if (arguments > 0)
        {
          change |= markArgument(commands, iter, index, iter.getNextIndex() + arguments);
        }
        else if (arguments == 0 || !iter.peekCommand().isReachable())
        {
          // argument == 0: manual defined JSR with zero-terminated argument
          // !commands.peekCommand().isReachable(): try automatic detection of zero-terminated argument
          int endIndex = search0(commands, iter, arguments != 0);
          if (endIndex < 0) {
            continue;
          }

          change |= markArgument(commands, iter, index, endIndex);
        }
      }
    }

    return change;
  }

  /**
   * Markus argument as data and following opcode as code.
   *
   * @param commands command buffer
   * @param index start index of JSR
   * @param endIndex end index of data (excl.), next opcode
   * @return whether a change has taken place
   */
  private boolean markArgument(CommandBuffer commands, CommandIterator iter, int index, int endIndex) {
    if (!commands.hasIndex(endIndex)) {
      return false;
    }

    boolean change = false;

    int startIndex = iter.getNextIndex();
    // Mark argument bytes as data
    change |= commands.setType(startIndex, endIndex, CodeType.DATA);
    // At endIndex the code continues
    change |= commands.setType(endIndex, CodeType.OPCODE);
    // Add reference to make code reachable
    commands.addCodeReference(index, commands.addressForIndex(endIndex));

    return change;
  }

  /**
   * Search end of zero-terminated argument.
   * Stops after 256 Bytes.
   *
   * @param commands command buffer
   * @param stopAtLabels search at labels?
   * @return end index or -1, if no arguments have been found
   */
  private int search0(CommandBuffer commands, CommandIterator iter, boolean stopAtLabels) {
    Assert.notNull(commands, "Precondition: commands != null");

    byte[] code = commands.getCode();
    for (int index = iter.getNextIndex(), count = 0; commands.hasIndex(index) && count < maxLength; index++)
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

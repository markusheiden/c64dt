package de.heiden.c64dt.assembler.detector;

import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.assembler.Opcode;
import de.heiden.c64dt.assembler.OpcodeType;
import de.heiden.c64dt.assembler.command.BitCommand;
import de.heiden.c64dt.assembler.command.CommandBuffer;
import de.heiden.c64dt.assembler.command.ICommand;
import de.heiden.c64dt.assembler.command.OpcodeCommand;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Detects jsr commands which are followed by their zero-terminated arguments.
 */
public class Jsr0Detector implements IDetector
{
  /**
   * Maximum length of zero-terminated argument after JSR opcode.
   */
  private final int maxLength = 256;

  @Override
  public boolean detect(CommandBuffer commands)
  {
    boolean change = false;

    commands.restart();
    int lastIndex = 0;
    ICommand lastCommand = null;
    while (commands.hasNextCommand())
    {
      int index = commands.getCurrentIndex();
      ICommand command = commands.nextCommand();
      if (lastCommand instanceof OpcodeCommand)
      {
        OpcodeCommand opcodeCommand = (OpcodeCommand) lastCommand;

        if (opcodeCommand.getOpcode().getType().equals(OpcodeType.JSR) && !command.isReachable())
        {
          int endIndex = search0(commands, index);
          if (endIndex >= 0) {
            // Mark argument bytes as data
            commands.setType(index, endIndex, CodeType.DATA);
            // At endIndex the code continues
            commands.setType(endIndex, CodeType.OPCODE);
            // Add reference to make code reachable
            commands.addCodeReference(lastIndex, commands.addressForIndex(endIndex));
            change = true;
          }
        }
      }

      lastIndex = index;
      lastCommand = command;
    }

    return change;
  }

  /**
   * Search end of zero-terminated argument.
   * Stops after 256 Bytes.
   *
   * @param commands command buffer
   * @param startIndex index to start at
   * @return end index or -1, if no arguments have been found
   */
  private int search0(CommandBuffer commands, int startIndex) {
    Assert.notNull(commands, "Precondition: commands != null");

    byte[] code = commands.getCode();
    for (int index = startIndex, count = 0; index < code.length && count < maxLength; index++)
    {
      if (commands.hasLabel(commands.addressForIndex(index))) {
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

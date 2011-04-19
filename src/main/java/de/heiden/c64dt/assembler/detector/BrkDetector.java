package de.heiden.c64dt.assembler.detector;

import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.assembler.OpcodeType;
import de.heiden.c64dt.assembler.command.CommandBuffer;
import de.heiden.c64dt.assembler.command.ICommand;
import de.heiden.c64dt.assembler.command.OpcodeCommand;

/**
 * Detects unreachable brk commands as data.
 */
public class BrkDetector implements IDetector
{
  @Override
  public boolean detect(CommandBuffer commands)
  {
    boolean change = false;

    commands.restart();
    while (commands.hasNextCommand())
    {
      ICommand command = commands.nextCommand();
      int index = commands.getCurrentIndex();
      if (command instanceof OpcodeCommand)
      {
        OpcodeCommand opcodeCommand = (OpcodeCommand) command;

        if (opcodeCommand.getOpcode().getType().equals(OpcodeType.BRK) && !opcodeCommand.isReachable())
        {
          commands.setType(index, CodeType.DATA);
        }
      }
    }

    return change;
  }
}

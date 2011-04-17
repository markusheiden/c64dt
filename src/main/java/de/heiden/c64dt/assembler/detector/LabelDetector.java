package de.heiden.c64dt.assembler.detector;

import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.assembler.command.CommandBuffer;
import de.heiden.c64dt.assembler.command.ICommand;

/**
 * Detects code label as code and data label as data.
 */
public class LabelDetector implements IDetector
{
  @Override
  public boolean detect(CommandBuffer commands)
  {
    boolean change = false;

    commands.restart();
    while (commands.hasNextCommand())
    {
      ICommand command = commands.nextCommand();
      if (commands.hasCodeLabel())
      {
        // Mark all code label positions as a start of an opcode
        commands.setType(CodeType.OPCODE);
        change = true;
      }
//      else if (commands.hasDataLabel() && commands.getType().isUnknown())
//      {
//        // Mark all data label positions as data
//        commands.setType(CodeType.DATA);
//        change = true;
//      }
    }

    return change;
  }
}
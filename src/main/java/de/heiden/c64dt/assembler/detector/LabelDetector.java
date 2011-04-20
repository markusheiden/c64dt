package de.heiden.c64dt.assembler.detector;

import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.assembler.command.CommandBuffer;
import de.heiden.c64dt.assembler.command.CommandIterator;
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

    for (CommandIterator iter = new CommandIterator(commands); iter.hasNextCommand();)
    {
      ICommand command = iter.nextCommand();
      if (iter.hasCodeLabel())
      {
        // Mark all code label positions as a start of an opcode
        change |= iter.setType(CodeType.OPCODE);
      }
      else if (iter.hasConflictingCodeLabel())
      {
        // Mark current command as data, because it may not be an opcode
        change |= iter.setType(CodeType.DATA);

        // Search for code label and mark the relative address as an opcode
        boolean notFound = true;
        for (int index = iter.getIndex() + 1, count = 1; count < command.getSize(); index++, count++)
        {
          // TODO mh: move functionality to CommandBuffer: hasCodeLabel(int index)
          if (commands.hasCodeLabel(commands.addressForIndex(index)))
          {
            change |= commands.setType(index, CodeType.OPCODE);
            notFound = false;
          }
          else if (notFound)
          {
            // mark as data until first code label
            change |= commands.setType(index, CodeType.DATA);
          }
        }

      }
//      else if (commands.hasDataLabel() && commands.getType().isUnknown())
//      {
//        // Mark all data label positions as data
//        change |= commands.setType(CodeType.DATA);
//      }
    }

    return change;
  }
}

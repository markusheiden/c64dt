package de.heiden.c64dt.assembler.detector;

import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.assembler.OpcodeType;
import de.heiden.c64dt.assembler.command.CommandBuffer;
import de.heiden.c64dt.assembler.command.CommandIterator;
import de.heiden.c64dt.assembler.command.ICommand;
import de.heiden.c64dt.assembler.command.OpcodeCommand;

/**
 * Detects unreachable brk commands as data.
 */
public class BrkDetector implements IDetector {
  @Override
  public boolean detect(CommandBuffer commands) {
    boolean change = false;

    for (CommandIterator iter = commands.iterator(); iter.hasNext(); ) {
      ICommand command = iter.next();
      int index = iter.getIndex();
      if (command instanceof OpcodeCommand) {
        OpcodeCommand opcodeCommand = (OpcodeCommand) command;

        if (opcodeCommand.getOpcode().getType().equals(OpcodeType.BRK) && !opcodeCommand.isReachable()) {
          change |= commands.setType(index, CodeType.DATA);
        }
      }
    }

    return change;
  }
}
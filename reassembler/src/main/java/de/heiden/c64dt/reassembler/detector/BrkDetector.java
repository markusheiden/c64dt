package de.heiden.c64dt.reassembler.detector;

import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.assembler.OpcodeType;
import de.heiden.c64dt.reassembler.command.CommandBuffer;
import de.heiden.c64dt.reassembler.command.CommandIterator;
import de.heiden.c64dt.reassembler.command.ICommand;
import de.heiden.c64dt.reassembler.command.OpcodeCommand;

import static de.heiden.c64dt.assembler.OpcodeType.BRK;

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
      if (command instanceof OpcodeCommand opcodeCommand) {
        if (opcodeCommand.getOpcode().getType() == BRK && !opcodeCommand.isReachable()) {
          change |= commands.setType(index, CodeType.DATA);
        }
      }
    }

    return change;
  }
}

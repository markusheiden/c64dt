package de.heiden.c64dt.reassembler.detector;

import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.assembler.OpcodeType;
import de.heiden.c64dt.reassembler.command.CommandBuffer;
import de.heiden.c64dt.reassembler.command.CommandIterator;
import de.heiden.c64dt.reassembler.command.ICommand;
import de.heiden.c64dt.reassembler.command.OpcodeCommand;

import static de.heiden.c64dt.assembler.OpcodeType.BRK;

/**
 * Detects unreachable BRK commands as data.
 */
public class BrkDetector implements IDetector {
  @Override
  public boolean detect(CommandBuffer commands) {
    var change = false;

    for (var iter = commands.iterator(); iter.hasNext(); ) {
      var command = iter.next();
      var index = iter.getIndex();
      if (command instanceof OpcodeCommand opcodeCommand) {
        if (opcodeCommand.getOpcode().getType() == BRK && !opcodeCommand.isReachable()) {
          change |= commands.setType(index, CodeType.DATA);
        }
      }
    }

    return change;
  }
}

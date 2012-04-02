package de.heiden.c64dt.assembler.detector;

import de.heiden.c64dt.assembler.OpcodeType;
import de.heiden.c64dt.assembler.command.*;

/**
 * Detects reachability of code.
 * Computes transitive unreachability of commands.
 * Should be the second detector.
 */
public class Reachability implements IDetector {
  @Override
  public boolean detect(CommandBuffer commands) {
    // initially mark all opcodes as reachable
    for (CommandIterator iter = new CommandIterator(commands); iter.hasNextCommand(); ) {
      ICommand command = iter.nextCommand();
      command.setReachable(command instanceof OpcodeCommand || command instanceof BitCommand);
    }

    CommandIterator iter = new CommandIterator(commands).reverse();

    // trace backward from unreachable command to the previous
    ICommand lastCommand = new DummyCommand();
    while (iter.hasPreviousCommand()) {
      ICommand command = iter.previousCommand();
      /*
       * A code command is not reachable, if it leads to unreachable code.
       * Exception is JSR, because its argument may follow directly after the instruction.
       *
       * TODO mh: check JMP/JSR/Bxx targets for reachability?
       */
      if (!lastCommand.isReachable() &&
        command.isReachable() && !command.isEnd() && !isJsr(command) && !iter.getType().isCode()) {
        command.setReachable(false);
        iter.removeReference();
      }

      lastCommand = command;
    }

    // code types have not been changed
    return false;
  }

  /**
   * Check if command is a JSR.
   *
   * @param command Command
   */
  private boolean isJsr(ICommand command) {
    return command instanceof OpcodeCommand && ((OpcodeCommand) command).getOpcode().getType().equals(OpcodeType.JSR);
  }
}

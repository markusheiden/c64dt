package de.heiden.c64dt.assembler.detector;

import de.heiden.c64dt.assembler.command.CommandBuffer;
import de.heiden.c64dt.assembler.command.CommandIterator;
import de.heiden.c64dt.assembler.command.DummyCommand;
import de.heiden.c64dt.assembler.command.ICommand;
import de.heiden.c64dt.assembler.command.OpcodeCommand;

/**
 * Detects reachability of code.
 * Computes transitive unreachability of commands.
 * Should be the second detector.
 */
public class Reachability implements IDetector
{
  @Override
  public boolean detect(CommandBuffer commands)
  {
    // initially mark all opcodes as reachable
    for (CommandIterator iter = new CommandIterator(commands); iter.hasNextCommand();)
    {
      ICommand command = iter.nextCommand();
      command.setReachable(command instanceof OpcodeCommand);
    }

    boolean change;
    do
    {
      CommandIterator iter = new CommandIterator(commands).reverse();
      change = false;

      // trace backward from unreachable command to the previous
      ICommand lastCommand = new DummyCommand();
      while (iter.hasPreviousCommand())
      {
        ICommand command = iter.previousCommand();
        /*
         * A code command is not reachable, if it leads to unreachable code.
         * Exception: JSR which may be followed by argument data.
         *
         * TODO mh: check JMP/JSR/Bxx targets for reachability
         */
        if (!lastCommand.isReachable() &&
          command.isReachable() && !command.isEnd() && !iter.getType().isCode())
        {
          command.setReachable(false);
          // restart, if reference caused a wrong label in the already scanned code
          change |= iter.removeReference();
        }

        lastCommand = command;
      }
    } while (change);

    // code types have not been changed
    return false;
  }
}

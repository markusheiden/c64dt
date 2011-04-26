package de.heiden.c64dt.assembler.detector;

import de.heiden.c64dt.assembler.command.CommandBuffer;
import de.heiden.c64dt.assembler.command.CommandIterator;
import de.heiden.c64dt.assembler.command.DummyCommand;
import de.heiden.c64dt.assembler.command.ICommand;

/**
 * Detects reachability of code.
 * Computes transitive unreachability of commands.
 */
public class ReachabilityDetector implements IDetector
{
  @Override
  public boolean detect(CommandBuffer commands)
  {
    boolean change;
    do
    {
      change = false;

      CommandIterator iter = new CommandIterator(commands);

      // tracing forward from one unreachable command to the next
      ICommand lastCommand = new DummyCommand();
      while (iter.hasNextCommand())
      {
        ICommand command = iter.nextCommand();
//        /*
//         * A command is not reachable, if the previous command is not reachable or is an ending command (e.g. JMP) and
//         * there is no code label for the command and the command has not already been detected as an opcode.
//         */
//        if (command.isReachable() && !iter.hasCodeLabel() && !iter.getType().isCode() &&
//          (!lastCommand.isReachable() || lastCommand.isEnd()))
//        {
//          command.setReachable(false);
//          // restart, if reference caused a wrong label in the already scanned code
//          change |= iter.removeReference();
//        }

        lastCommand = command;
      }

      // trace backward from unreachable command to the previous
      lastCommand = new DummyCommand();
      while (iter.hasPreviousCommand())
      {
        ICommand command = iter.previousCommand();
        /*
         * A code command is not reachable, if it leads to unreachable code.
         * Exception: JSR which may be followed by argument data.
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

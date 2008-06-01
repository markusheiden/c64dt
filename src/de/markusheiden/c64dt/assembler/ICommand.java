package de.markusheiden.c64dt.assembler;

import java.io.Writer;
import java.io.IOException;

/**
 * Command.
 */
public interface ICommand {
  /**
   * Size in bytes of this command.
   */
  public int getSize();

  /**
   * Is the command right after this command not reachable from this command?
   */
  public boolean isEnd();

  /**
   * Is this command reachable?
   */
  public boolean isReachable();

  /**
   * Set whether this command is reachable.
   *
   * @param reachable is this command reachable?
   */
  public void setReachable(boolean reachable);

  /**
   * Combine this command with the given command.
   *
   * @param command command right after this command
   * @return returns if commands have been combined
   */
  public boolean combineWith(ICommand command);

  /**
   * Write string representation of this command.
   *
   * @param buffer context
   * @param output writer to write ti
   */
  public void toString(CodeBuffer buffer, Writer output) throws IOException;
}

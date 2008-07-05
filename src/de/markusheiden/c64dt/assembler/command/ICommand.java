package de.markusheiden.c64dt.assembler.command;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * Command.
 */
public interface ICommand {
  /**
   * Size in bytes of this command.
   */
  public int getSize();

  /**
   * Has the command an address set?.
   */
  public boolean hasAddress();

  /**
   * The address of the command.
   */
  public int getAddress();

  /**
   * Set the address.
   *
   * @param address address
   */
  public void setAddress(int address);

  /**
   * The first address after this command.
   */
  public int getNextAddress();

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
   * @param buffer command buffer
   * @param output writer to write ti
   */
  public void toString(CommandBuffer buffer, Writer output) throws IOException;

  /**
   * Byte representation of this command.
   */
  public List<Integer> toBytes();
}

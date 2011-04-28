package de.heiden.c64dt.assembler.command;

import de.heiden.c64dt.assembler.CodeType;

import java.io.IOException;
import java.util.List;

/**
 * Command.
 */
public interface ICommand
{
  /**
   * Get code type this command handles.
   */
  public CodeType getType();

  /**
   * Size in bytes of this command.
   */
  public int getSize();

  /**
   * Has the command an address set?.
   */
  public boolean hasAddress();

  /**
   * The absolute address of the command.
   */
  public int getAddress();

  /**
   * Set the address.
   *
   * @param address absolute address
   */
  public void setAddress(int address);

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
   */
  public String toString(CommandBuffer buffer);

  /**
   * Byte representation of this command.
   */
  public List<Integer> toBytes();
}

package de.heiden.c64dt.reassembler.command;

import de.heiden.c64dt.assembler.CodeType;

import java.util.List;

/**
 * Command.
 */
public interface ICommand {
  /**
   * Get code type this command handles.
   */
  CodeType getType();

  /**
   * Size in bytes of this command.
   */
  int getSize();

  /**
   * Has the command an address set?.
   */
  boolean hasAddress();

  /**
   * The absolute address of the command.
   */
  int getAddress();

  /**
   * Set the address.
   *
   * @param address absolute address
   */
  void setAddress(int address);

  /**
   * Is the command right after this command not reachable from this command?
   */
  boolean isEnd();

  /**
   * Is this command reachable?
   */
  boolean isReachable();

  /**
   * Set whether this command is reachable.
   *
   * @param reachable is this command reachable?
   */
  void setReachable(boolean reachable);

  /**
   * Combine this command with the given command.
   *
   * @param command command right after this command
   * @return returns if commands have been combined
   */
  boolean combineWith(ICommand command);

  /**
   * Write string representation of this command.
   *
   * @param buffer command buffer
   */
  String toString(CommandBuffer buffer);

  /**
   * Byte representation of this command.
   */
  List<Integer> toBytes();
}

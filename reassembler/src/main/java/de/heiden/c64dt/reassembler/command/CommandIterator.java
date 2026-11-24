package de.heiden.c64dt.reassembler.command;

import java.util.Iterator;
import java.util.Objects;

import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.reassembler.label.ILabel;
import org.jspecify.annotations.Nullable;

import static com.github.cowwoc.requirements10.java.DefaultJavaValidators.requireThat;

/**
 * Command iterator.
 */
public class CommandIterator implements Iterator<ICommand> {
  /**
   * The command buffer to iterate.
   */
  private final CommandBuffer commands;

  /**
   * Index of the current command / the iterator.
   * Start before the first command.
   */
  private int index = -1;

  /**
   * Constructor.
   *
   * @param commands Command buffer to iterate.
   */
  public CommandIterator(CommandBuffer commands) {
    this.commands = commands;
  }

  /**
   * Start iteration at the last command.
   *
   * @return this, for method chaining.
   */
  public CommandIterator reverse() {
    // Trace backward for last command.
    for (index = commands.getLength() - 1; index >= 0 && commands.getCommand(index) == null; index--) {
      // Search further.
    }
    // Set index after last command.
    index = getNextIndex();

    return this;
  }

  //
  // Code types.
  //

  /**
   * Get code type of the current opcode / command.
   */
  public CodeType getType() {
    return commands.getType(index);
  }

  /**
   * Set code type for the current opcode / command.
   *
   * @param type Code type.
   * @return Whether a change has taken place.
   */
  public boolean setType(CodeType type) {
    return commands.setType(index, type);
  }

  //
  // Commands.
  //

  /**
   * The current relative address.
   */
  public int getIndex() {
    return index;
  }

  /**
   * Sets the current relative address.
   *
   * @param index Relative address.
   */
  public void setIndex(int index) {
    // Check that there is a command at the given index.
    requireThat(commands.getCommand(index), "commands.getCommand(index)").isNotNull();

    this.index = index;
  }

  /**
   * The current absolute address.
   */
  public int getAddress() {
    return commands.addressForIndex(index);
  }

  /**
   * The next relative address.
   * This is the index from where the next command will be read.
   */
  public int getNextIndex() {
    return index < 0 ? 0 : index + getCommand().getSize();
  }

  /**
   * Is there one more command?
   */
  @Override
  public boolean hasNext() {
    return commands.hasIndex(getNextIndex());
  }

  /**
   * Iterate to next command and return it.
   */
  @Override
  public ICommand next() {
    index = getNextIndex();
    var result = getCommand();

    return result;
  }

  /**
   * Get the current command.
   */
  public ICommand getCommand() {
    return Objects.requireNonNull(commands.getCommand(index));
  }

  /**
   * Get the next command without iterating to it.
   */
  public @Nullable ICommand peek() {
    var nextIndex = getNextIndex();
    return commands.hasIndex(nextIndex) ? commands.getCommand(nextIndex) : DummyCommand.DUMMY_COMMAND;
  }

  /**
   * Is there at least one command before the current command.
   */
  public boolean hasPrevious() {
    return index > 0;
  }

  /**
   * Iterate to the previous command and return it.
   */
  @SuppressWarnings({"StatementWithEmptyBody"})
  public ICommand previous() {
    // Get start of current command for consistency check.
    var endIndex = index;
    // Trace backwards for previous command.
    while (index > 0 && commands.getCommand(--index) == null) {
      // Search further.
    }
    var result = getCommand();

    // Check that the previous command ends at the start of the current command.
    requireThat(getNextIndex(), "getNextIndex()").isEqualTo(endIndex);
    return result;
  }

  //
  // Modifying operations during iteration.
  //

  /**
   * Removes the current command.
   * Traces back to the previous commands afterwards.
   */
  @Override
  public void remove() {
    // Remember position of current command.
    var remove = index;
    // Skip current command.
    index = getNextIndex();
    // Delete current command.
    commands.removeCommand(remove);
    // Trace back to previous command.
    previous();
  }

  //
  // Label/reference specific interface.
  //

  /**
   * Is a label at the current opcode / command?
   */
  public boolean hasLabel() {
    return commands.hasLabel(getCommand().getAddress());
  }

  /**
   * Label representation for the current address.
   *
   * @return Label representation or null if no label exists for the current address.
   */
  public @Nullable ILabel getLabel() {
    return commands.getLabel(getCommand().getAddress());
  }

  /**
   * Is a code label at the current opcode / command?
   */
  public boolean hasCodeLabel() {
    return commands.hasCodeLabel(getCommand().getAddress());
  }

  /**
   * Is a data label at the current opcode / command?
   */
  public boolean hasDataLabel() {
    return commands.hasDataLabel(getCommand().getAddress());
  }

  /**
   * Remove a reference from the current address.
   *
   * @return Whether a label has been removed.
   */
  public boolean removeReference() {
    return commands.removeReference(index);
  }
}

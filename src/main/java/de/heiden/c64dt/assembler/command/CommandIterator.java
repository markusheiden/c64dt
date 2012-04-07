package de.heiden.c64dt.assembler.command;

import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.assembler.label.ILabel;
import org.springframework.util.Assert;

import java.util.Iterator;

/**
 * Command iterator.
 */
public class CommandIterator implements Iterator<ICommand>
{
  /**
   * The command buffer to iterate.
   */
  private CommandBuffer commands;

  /**
   * Index of the current command / the iterator.
   * Start before the first command.
   */
  private int index = -1;

  /**
   * Constructor.
   *
   * @param commands Command buffer to iterate
   */
  public CommandIterator(CommandBuffer commands)
  {
    Assert.notNull(commands, "Precondition: commands != null");

    this.commands = commands;
  }

  /**
   * Start iteration at the last command.
   *
   * @return this, for method chaining
   */
  public CommandIterator reverse()
  {
    // trace backward for last command
    for (index = commands.getLength() - 1; index >= 0 && commands.getCommand(index) == null; index--)
    {
      // search further
    }
    // set index after last command
    index = getNextIndex();

    return this;
  }

  //
  // code types
  //

  /**
   * Get code type of the current opcode / command.
   */
  public CodeType getType()
  {
    return commands.getType(index);
  }

  /**
   * Set code type for the current opcode / command.
   *
   * @param type code type
   * @return whether a change has taken place
   */
  public boolean setType(CodeType type)
  {
    return commands.setType(index, type);
  }

  //
  // commands
  //

  /**
   * The current relative address.
   */
  public int getIndex()
  {
    return index;
  }

  /**
   * Sets the current relative address.
   *
   * @param index relative address
   */
  public void setIndex(int index)
  {
    Assert.notNull(commands.getCommand(index), "Precondition: There is a command at the given index");

    this.index = index;
  }

  /**
   * The current absolute address.
   */
  public int getAddress()
  {
    return commands.addressForIndex(index);
  }

  /**
   * The next relative address.
   * This is the index from where the next command will be read.
   */
  public int getNextIndex()
  {
    return index < 0? 0 : index + getCommand().getSize();
  }

  /**
   * Is there one more command?
   */
  public boolean hasNext()
  {
    return commands.hasIndex(getNextIndex());
  }

  /**
   * Iterate to next command and return it.
   */
  public ICommand next()
  {
    index = getNextIndex();
    ICommand result = getCommand();

    Assert.notNull(result, "Postcondition: result != null");
    return result;
  }

  /**
   * Get the current command.
   */
  public ICommand getCommand()
  {
    return commands.getCommand(index);
  }

  /**
   * Get the next command without iterating to it.
   */
  public ICommand peek()
  {
    int nextIndex = getNextIndex();
    return commands.hasIndex(nextIndex)? commands.getCommand(nextIndex) : DummyCommand.DUMMY_COMMAND;
  }

  /**
   * Is there at least one command before the current command.
   */
  public boolean hasPrevious()
  {
    return index > 0;
  }

  /**
   * Iterate to the previous command and return it.
   */
  @SuppressWarnings({"StatementWithEmptyBody"})
  public ICommand previous()
  {
    // get start of current command for consistency check
    int endIndex = index;
    // trace backwards for previous command
    while (index > 0 && commands.getCommand(--index) == null)
    {
      // search further
    }
    ICommand result = getCommand();

    Assert.notNull(result, "Postcondition: result != null");
    Assert.isTrue(getNextIndex() == endIndex, "Precondition: The previous commands ends at the start of the current command");
    return result;
  }

  //
  // modifying operations during iteration
  //

  /**
   * Removes the current command.
   * Traces back to the previous commands afterwards.
   */
  public void remove()
  {
    // remember position of current command
    int remove = index;
    // skip current command
    index = getNextIndex();
    // delete current command
    commands.removeCommand(remove);
    // trace back to previous command
    previous();
  }

  //
  // label/reference specific interface
  //

  /**
   * Is a label at the current opcode / command?
   */
  public boolean hasLabel()
  {
    return commands.hasLabel(getCommand().getAddress());
  }

  /**
   * Label representation for the current address.
   *
   * @return label representation or null if no label exists for the current address
   */
  public ILabel getLabel()
  {
    return commands.getLabel(getCommand().getAddress());
  }

  /**
   * Is a code label at the current opcode / command?
   */
  public boolean hasCodeLabel()
  {
    return commands.hasCodeLabel(getCommand().getAddress());
  }

  /**
   * Is a data label at the current opcode / command?
   */
  public boolean hasDataLabel()
  {
    return commands.hasDataLabel(getCommand().getAddress());
  }

  /**
   * Remove a reference from the current address.
   *
   * @return whether a label has been removed
   */
  public boolean removeReference()
  {
    return commands.removeReference(index);
  }
}

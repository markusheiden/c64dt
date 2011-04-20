package de.heiden.c64dt.assembler.command;

import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.assembler.ILabel;
import org.springframework.util.Assert;

/**
 * Command iterator.
 */
public class CommandIterator
{
  /**
   * The command buffer to iterate.
   */
  private CommandBuffer commands;

  /**
   * Index of the current command.
   * Iterator.
   */
  private int index;

  /**
   * Constructor.
   *
   * @param commands Command buffer to iterate
   */
  public CommandIterator(CommandBuffer commands)
  {
    this.commands = commands;
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
   * The next relative address.
   * This is the index from where the next command will be read.
   */
  public int getNextIndex()
  {
    return index + getCommand().getSize();
  }

  /**
   * Is there one more command?
   */
  public boolean hasNextCommand()
  {
    return commands.hasIndex(getNextIndex());
  }

  /**
   * Iterate to next command and return it.
   */
  public ICommand nextCommand()
  {
    index = getNextIndex();
    ICommand result = getCommand();

    Assert.notNull(result, "Postcondition: result != null");
    return result;
  }

  /**
   * Get the current command.
   */
  private ICommand getCommand()
  {
    return commands.getCommand(index);
  }

  /**
   * Get the next command without iterating to it.
   */
  public ICommand peekCommand() {
    int nextIndex = getNextIndex();
    return commands.hasIndex(nextIndex)? commands.getCommand(nextIndex) : DummyCommand.DUMMY_COMMAND;
  }

  /**
   * Is there at least one command before the current command.
   */
  public boolean hasPreviousCommand()
  {
    return index > 0;
  }

  /**
   * Iterate to the previous command and return it.
   */
  @SuppressWarnings({"StatementWithEmptyBody"})
  public ICommand previousCommand()
  {
    // get start of current command for consistency check
    int endIndex = index;
    // trace back for previous command
    while (index > 0 && commands.getCommand(--index) == null);
    ICommand result = getCommand();

    Assert.notNull(result, "Postcondition: result != null");
    Assert.isTrue(getNextIndex() == endIndex, "Precondition: The previous commands ends at the start of the current command");
    return result;
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
   * Is there at least one code label pointing to the argument of the current opcode / command?
   */
  public boolean hasConflictingCodeLabel()
  {
    for (int address = commands.addressForIndex(index) + 1, count = 1; count < getCommand().getSize(); address++, count++)
    {
      if (commands.hasCodeLabel(address)) {
        return true;
      }
    }

    return false;
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


  //
  // modifying operations during iteration
  //

  /**
   * Removes the current command.
   * Traces back to the previous commands afterwards.
   */
  public void removeCurrentCommand()
  {
    // remember position of current command
    int remove = index;
    // skip current command
    index = getNextIndex();
    // delete current command
    commands.removeCommand(remove);
    // trace back to previous command
    previousCommand();
  }
}

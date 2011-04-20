package de.heiden.c64dt.assembler.command;

import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.assembler.ILabel;
import org.springframework.util.Assert;

/**
 * Command iterator.
 */
public class CommandIterator
{
  private CommandBuffer commands;

  /**
   * Index of the last added command.
   */
  private int index;

  /**
   * Constructor.
   *
   * @param commands
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
   * (Re)start iteration.
   */
  public void restart()
  {
    index = 0;
  }

  public boolean hasNextCommand()
  {
    return commands.hasIndex(getNextIndex());
  }

  public ICommand nextCommand()
  {
    index = getNextIndex();
    ICommand result = commands.commands[index];

    Assert.notNull(result, "Postcondition: result != null");
    return result;
  }

  public ICommand peekCommand() {
    int nextIndex = getNextIndex();
    return commands.hasIndex(nextIndex)? commands.commands[nextIndex] : DummyCommand.DUMMY_COMMAND;
  }

  public boolean hasPreviousCommand()
  {
    return index > 0;
  }

  @SuppressWarnings({"StatementWithEmptyBody"})
  public ICommand previousCommand()
  {
    // get start of current command for consistency check
    int endIndex = index;
    // trace back for previous command
    while (index > 0 && commands.commands[--index] == null);
    ICommand result = commands.commands[index];

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
    ICommand current = commands.commands[index];
    return commands.hasLabel(current.getAddress());
  }

  /**
   * Label representation for the current address.
   *
   * @return label representation or null if no label exists for the current address
   */
  public ILabel getLabel()
  {
    ICommand current = commands.commands[index];
    return commands.getLabel(current.getAddress());
  }

  /**
   * Is there at least one code label pointing to the argument of the current opcode / command?
   */
  public boolean hasConflictingCodeLabel()
  {
    ICommand current = commands.commands[index];
    for (int address = commands.addressForIndex(index) + 1, count = 1; count < current.getSize(); address++, count++)
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
    ICommand current = commands.commands[index];
    return commands.hasCodeLabel(current.getAddress());
  }

  /**
   * Is a data label at the current opcode / command?
   */
  public boolean hasDataLabel()
  {
    ICommand current = commands.commands[index];
    return commands.hasDataLabel(current.getAddress());
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
  //
  //

  /**
   * The current relative address.
   * Only valid, if at least one command has been added.
   */
  public int getCurrentIndex()
  {
    return index;
  }

  /**
   * The next relative address.
   * This is the index where the next command will be added.
   */
  public int getNextIndex()
  {
    ICommand current = commands.commands[index];
    return index + current.getSize();
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
    commands.commands[remove] = null;
    // trace back to previous command
    previousCommand();
  }
}

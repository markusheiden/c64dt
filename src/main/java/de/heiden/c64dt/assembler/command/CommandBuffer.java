package de.heiden.c64dt.assembler.command;

import de.heiden.c64dt.assembler.CodeLabel;
import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.assembler.DataLabel;
import de.heiden.c64dt.assembler.ExternalLabel;
import de.heiden.c64dt.assembler.ILabel;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import static de.heiden.c64dt.assembler.command.DummyCommand.DUMMY_COMMAND;
import static de.heiden.c64dt.util.AddressUtil.assertValidAddress;

/**
 * Input stream for code.
 */
public class CommandBuffer
{
  /**
   * Index to type of code.
   */
  private final CodeType[] types;

  /**
   * Index to referenced absolute address.
   * Target is code, e.g. if the reference is from a jmp $xxxx.
   */
  private final int[] codeReferences;

  /**
   * Index to referenced absolute address.
   * Target is data, e.g. if the reference is from a sta $xxxx.
   */
  private final int[] dataReferences;


  /**
   * Index to referenced absolute address.
   * This references point to outside of the code.
   */
  private final int[] externalReferences;

  /**
   * Absolute address to code label.
   * If there is at least one reference to an address a label for it exists.
   */
  private final Map<Integer, CodeLabel> codeLabels;

  /**
   * Absolute address to data label.
   * If there is at least one reference to an address a label for it exists.
   */
  private final Map<Integer, DataLabel> dataLabels;

  /**
   * Absolute address to external label.
   * If there is at least one reference to an address a label for it exists.
   */
  private final Map<Integer, ExternalLabel> externalLabels;

  /**
   * Index to absolute base address.
   * First entry is always 0 -> initial start address.
   * Last entry is always length -> initial start address + length.
   */
  private final SortedMap<Integer, Integer> startAddresses;

  /**
   * Total length of code.
   */
  private final int length;

  /**
   * Commands as detected by the reassembler.
   */
  private final ICommand[] commands;

  /**
   * Current iterator over {@link #commands}.
   */
  private int index;

  /**
   * Constructor.
   *
   * @param length length of code
   * @param startAddress address of the code
   */
  public CommandBuffer(int length, int startAddress)
  {
    Assert.isTrue(startAddress >= 0, "Precondition: startAddress >= 0");

    this.types = new CodeType[length];
    Arrays.fill(this.types, CodeType.UNKNOWN);
    this.codeReferences = new int[length];
    Arrays.fill(this.codeReferences, -1);
    this.dataReferences = new int[length];
    Arrays.fill(this.dataReferences, -1);
    this.externalReferences = new int[length];
    Arrays.fill(this.externalReferences, -1);

    this.codeLabels = new HashMap<Integer, CodeLabel>();
    this.dataLabels = new HashMap<Integer, DataLabel>();
    this.externalLabels = new HashMap<Integer, ExternalLabel>();
    this.startAddresses = new TreeMap<Integer, Integer>();
    this.commands = new ICommand[length];
    this.commands[0] = new DummyCommand();
    this.index = 0;

    this.length = length;
    this.startAddresses.put(0, startAddress);
    this.startAddresses.put(length, startAddress + length);
  }

  /**
   * Is the given relative address valid?.
   *
   * @param index relative address
   */
  public boolean isValidIndex(int index)
  {
    return index >= 0 && index <= length;
  }

  /**
   * The current relative address.
   * This is the index where the next command will be added.
   */
  public int getCurrentIndex()
  {
    ICommand current = commands[index];
    return index + current.getSize();
  }

  /**
   * Start address of code (incl.).
   */
  public int getStartAddress()
  {
    return startAddresses.get(0);
  }

  /**
   * Change the base address of the code starting at the given index.
   *
   * @param startIndex relative address from which the new absolute base address should be used
   * @param baseAddress new absolute base address
   */
  public void rebase(int startIndex, int baseAddress)
  {
    Assert.isTrue(isValidIndex(startIndex), "Precondition: isValidIndex(startIndex)");
    assertValidAddress(baseAddress);

    Integer removed = startAddresses.put(startIndex, baseAddress);
    Assert.isNull(removed, "Precondition: Not rebased the same index twice");
  }

  //
  // code type specific stuff ("model")
  //

  /**
   * Get code type of the current opcode / command.
   */
  public CodeType getType()
  {
    return getType(index);
  }

  /**
   * Get code type of the command at the given relative address.
   *
   * @param index relative address
   */
  public CodeType getType(int index)
  {
    Assert.isTrue(isValidIndex(index), "Precondition: isValidIndex(index)");

    return types[index];
  }

  /**
   * Set code type for the current opcode / command.
   *
   * @param type code type
   */
  public void setType(CodeType type)
  {
    setType(index, type);
  }

  /**
   * Set code type for a given relative address range.
   *
   * @param startIndex first relative address (incl.)
   * @param endIndex last relative address (excl.)
   * @param type code type
   */
  public void setType(int startIndex, int endIndex, CodeType type)
  {
    Assert.isTrue(isValidIndex(startIndex), "Precondition: isValidIndex(startIndex)");
    Assert.isTrue(isValidIndex(endIndex), "Precondition: isValidIndex(endIndex)");
    Assert.isTrue(startIndex <= endIndex, "Precondition: startIndex <= endIndex");
    Assert.notNull(type, "Precondition: type != null");

    for (int index = startIndex; index < endIndex; index++)
    {
      setType(index, type);
    }
  }

  /**
   * Set code type for a given relative address.
   *
   * @param index relative address
   * @param type code type
   */
  public void setType(int index, CodeType type)
  {
    Assert.isTrue(isValidIndex(index), "Precondition: isValidIndex(index)");
    Assert.notNull(type, "Precondition: type != null");

    types[index] = type;
  }

  //
  // label/reference specific interface
  //

  /**
   * Is a label at the current opcode / command?
   */
  public boolean hasLabel()
  {
    ICommand current = commands[index];
    return getType().isCode() ? hasCodeLabel(current.getAddress()) : hasDataLabel(current.getAddress());
  }

  /**
   * Is a code label at the current opcode / command?
   */
  public boolean hasCodeLabel()
  {
    ICommand current = commands[index];
    return hasCodeLabel(current.getAddress());
  }

  /**
   * Is a code label at the given absolute address?
   *
   * @param address absolute address
   */
  public boolean hasCodeLabel(int address)
  {
    return codeLabels.containsKey(address);
  }

  /**
   * Is a data label at the current opcode / command?
   */
  public boolean hasDataLabel()
  {
    ICommand current = commands[index];
    return hasDataLabel(current.getAddress());
  }

  /**
   * Is a data label at the given absolute address?
   *
   * @param address absolute address
   */
  public boolean hasDataLabel(int address)
  {
    return dataLabels.containsKey(address);
  }

  /**
   * Is the given absolute address within the code?.
   *
   * @param address absolute address
   */
  public boolean hasAddress(int address)
  {
    Iterator<Entry<Integer, Integer>> iter = startAddresses.entrySet().iterator();
    Entry<Integer, Integer> lastAddressEntry = iter.next();
    while (iter.hasNext())
    {
      Entry<Integer, Integer> addressEntry = iter.next();
      if (address >= lastAddressEntry.getValue() + lastAddressEntry.getKey() &&
        address < lastAddressEntry.getValue() + addressEntry.getKey())
      {
        return true;
      }
      lastAddressEntry = addressEntry;
    }

    return false;
  }

  /**
   * Compute absolute address from relative address.
   *
   * @param index relative address
   * @return absolute address
   */
  public int addressForIndex(int index)
  {
    Assert.isTrue(isValidIndex(index), "Precondition: isValidIndex(index)");

    int lastStartIndex = startAddresses.firstKey();
    for (int startIndex : startAddresses.keySet())
    {
      if (index < startIndex)
      {
        return startAddresses.get(lastStartIndex) + index;
      }

      lastStartIndex = startIndex;
    }

    throw new IllegalArgumentException("May not happen");
  }

  /**
   * Add a reference from the given relative address to a given address.
   * This will add a label if no label exists for the given address.
   *
   * @param code is it a code reference?
   * @param fromIndex relative address of the command referencing
   * @param to referenced absolute address
   */
  public void addReference(boolean code, int fromIndex, int to)
  {
    Assert.isTrue(isValidIndex(fromIndex), "Precondition: isValidIndex(fromIndex)");

    if (!hasAddress(to))
    {
      addExternalReference(fromIndex, to);
    }
    else if (code)
    {
      addCodeReference(fromIndex, to);
    }
    else
    {
      addDataReference(fromIndex, to);
    }
  }

  /**
   * Add a code reference from the current address to a given address.
   * This will add a label if no label exists for the given address.
   *
   * @param fromIndex relative address of the command referencing
   * @param to referenced absolute address
   */
  public void addCodeReference(int fromIndex, int to)
  {
    Assert.isTrue(isValidIndex(fromIndex), "Precondition: isValidIndex(fromIndex)");
    Assert.isTrue(hasAddress(to), "Precondition: hasAddress(to)");

    // add label for address "to"
    codeLabels.put(to, new CodeLabel(to));
    // add reference
    codeReferences[fromIndex] = to;
  }

  /**
   * Add a data reference from the current address to a given address.
   * This will add a label if no label exists for the given address.
   *
   * @param fromIndex relative address of the command referencing
   * @param to referenced absolute address
   */
  public void addDataReference(int fromIndex, int to)
  {
    Assert.isTrue(isValidIndex(fromIndex), "Precondition: isValidIndex(fromIndex)");
    Assert.isTrue(hasAddress(to), "Precondition: hasAddress(to)");

    // add label for address "to"
    dataLabels.put(to, new DataLabel(to));
    // add reference
    dataReferences[fromIndex] = to;
  }

  /**
   * Add an external code or data reference from the current address to a given address.
   * This will add a label if no label exists for the given address.
   *
   * @param fromIndex relative address of the command referencing
   * @param to referenced absolute address
   */
  public void addExternalReference(int fromIndex, int to)
  {
    Assert.isTrue(isValidIndex(fromIndex), "Precondition: isValidIndex(fromIndex)");
    Assert.isTrue(!hasAddress(to), "Precondition: !hasAddress(to)");

    // add label for address "to"
    externalLabels.put(to, new ExternalLabel(to));
    // add reference
    externalReferences[fromIndex] = to;
  }

  /**
   * Remove a reference from the current address.
   *
   * @return whether a label has been removed
   */
  public boolean removeReference()
  {
    return
      remove(index, codeReferences, codeLabels) |
        remove(index, dataReferences, dataLabels) |
        remove(index, externalReferences, externalLabels);
  }

  /**
   * Remove a reference from the given relative address
   *
   * @param index relative address
   * @param references all references
   * @param labels all labels
   * @return whether a label has been removed
   */
  private boolean remove(int index, int[] references, Map<Integer, ?> labels)
  {
    // get referenced absolute address
    int reference = references[index];
    // delete reference
    references[index] = -1;

    if (reference < 0)
    {
      // if nothing has been referenced, no label needs to be removed
      return false;
    }

    // check if referenced address is referenced from elsewhere too
    for (int i = 0; i < references.length; i++)
    {
      if (references[i] == reference)
      {
        // referenced address is still referenced, no label needs to be removed
        return false;
      }
    }

    // remove label, because the address is no more referenced
    Object removed = labels.remove(reference);
    Assert.notNull(removed, "Check: There need to be a label if there had been a reference");

    // label has been removed
    return true;
  }

  /**
   * Label representation for the current address.
   *
   * @return label representation or null if no label exists for the current address
   */
  public ILabel getLabel()
  {
    ICommand current = commands[index];
    return getLabel(current.getAddress());
  }

  /**
   * Label representation for an absolute address.
   *
   * @param address absolute address
   * @return label or null if no label exists for this address
   */
  public ILabel getLabel(int address)
  {
    ILabel result = codeLabels.get(address);
    if (result == null)
    {
      result = dataLabels.get(address);
    }
    if (result == null)
    {
      result = externalLabels.get(address);
    }

    return result;
  }

  /**
   * All referenced addresses which do not point to this code.
   */
  public Collection<ExternalLabel> getExternalLabels()
  {
    return externalLabels.values();
  }

  //
  // command specific interface
  //

  /**
   * Clear all commands, references and labels.
   */
  public void clear()
  {
    Arrays.fill(codeReferences, -1);
    Arrays.fill(dataReferences, -1);
    Arrays.fill(externalReferences, -1);
    codeLabels.clear();
    dataLabels.clear();
    externalLabels.clear();
    Arrays.fill(commands, null);
    commands[0] = new DummyCommand();
    index = 0;

  }

  /**
   * Add a command at the end of the buffer.
   *
   * @param command command
   */
  public void addCommand(ICommand command)
  {
    Assert.notNull(command, "Precondition: command != null");
    Assert.isTrue(!command.hasAddress(), "Precondition: !command.hasAddress()");

    index = getCurrentIndex();
    addCommand(index, command);
  }

  /**
   * Add a command at a given relative address.
   *
   * @param index relative address
   * @param command command
   */
  private void addCommand(int index, ICommand command)
  {
    Assert.notNull(command, "Precondition: command != null");
    Assert.isTrue(!command.hasAddress(), "Precondition: !command.hasAddress()");

    int address = addressForIndex(index);
    Assert.isTrue(address >= 0, "Precondition: address >= 0");

    command.setAddress(address);
    commands[index] = command;
  }

  //
  // Iterator
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
    return getCurrentIndex() < length;
  }

  public ICommand nextCommand()
  {
    index = getCurrentIndex();
    ICommand result = commands[index];

    Assert.notNull(result, "Postcondition: result != null");
    return result;
  }

  public boolean hasPreviousCommand()
  {
    return index > 0;
  }

  public ICommand previousCommand()
  {
    // get start of current command for consistency check
    int endIndex = index;
    // trace back for previous command
    while (index > 0 && commands[--index] == null);
    ICommand result = commands[index];

    Assert.notNull(result, "Postcondition: result != null");
    Assert.isTrue(getCurrentIndex() == endIndex, "Precondition: The previous commands ends at the start of the current command");
    return result;
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
    index = getCurrentIndex();
    // delete current command
    commands[remove] = null;
    // trace back to previous command
    previousCommand();
  }

  /**
   * Replace the current command.
   *
   * @param replacements Replacement commands
   */
  public void replaceCurrentCommand(ICommand... replacements)
  {
    // get end of command for consistency check
    int endIndex = getCurrentIndex();

    // delete old command
    commands[index] = null;

    // add new commands
    for (ICommand replacement : replacements)
    {
      addCommand(index, replacement);
      index += replacement.getSize();
    }
    previousCommand();

    // check consistency
    Assert.isTrue(getCurrentIndex() == endIndex, "Precondition: The size of the replacements is equal to the size of the removed command");
  }
}

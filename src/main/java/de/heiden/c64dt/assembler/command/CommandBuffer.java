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
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;

import static de.heiden.c64dt.util.AddressUtil.assertValidAddress;

/**
 * Input stream for code.
 */
public class CommandBuffer
{
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

  //
  // persistent attributes
  //

  /**
   * Code to be reassembled.
   */
  private final byte[] code;

  /**
   * Index to type of code.
   */
  private final CodeType[] types;

  /**
   * Index to absolute base address.
   * First entry is always 0 -> initial start address.
   * Last entry is always length -> initial start address.
   */
  private final NavigableMap<Integer, Integer> startAddresses;

  /**
   * Index to number of argument bytes the subroutines takes.
   */
  private final Map<Integer, Integer> subroutines;

  /**
   * Commands as detected by the reassembler.
   */
  private final ICommand[] commands;

  //
  //
  //

  /**
   * Index of the last added command.
   */
  private int index;

  /**
   * Constructor.
   *
   * @param code code
   * @param startAddress address of the code
   */
  public CommandBuffer(byte[] code, int startAddress)
  {
    Assert.isTrue(startAddress >= 0, "Precondition: startAddress >= 0");

    this.codeReferences = new int[code.length];
    Arrays.fill(this.codeReferences, -1);
    this.dataReferences = new int[code.length];
    Arrays.fill(this.dataReferences, -1);
    this.externalReferences = new int[code.length];
    Arrays.fill(this.externalReferences, -1);
    this.codeLabels = new HashMap<Integer, CodeLabel>();
    this.dataLabels = new HashMap<Integer, DataLabel>();
    this.externalLabels = new HashMap<Integer, ExternalLabel>();

    this.code = code;
    this.types = new CodeType[code.length];
    Arrays.fill(this.types, CodeType.UNKNOWN);
    this.startAddresses = new TreeMap<Integer, Integer>();
    this.startAddresses.put(0, startAddress);
    this.startAddresses.put(code.length, startAddress);
    this.subroutines = new HashMap<Integer, Integer>();
    this.commands = new ICommand[code.length];
    this.commands[0] = new DummyCommand();

    this.index = 0;

  }

  /**
   * Code to be reassembled.
   */
  public byte[] getCode()
  {
    return code;
  }

  /**
   * Length of code.
   */
  public int getLength() {
    return code.length;
  }

  /**
   * Start address of code (incl.).
   */
  public int getStartAddress()
  {
    return startAddresses.get(0);
  }

  /**
   * Get all start addresses.
   * Just for the mapper.
   */
  SortedMap<Integer, Integer> getStartAddresses() {
    return startAddresses;
  }

  /**
   * Change the absolute base address of the code starting at the given index.
   *
   * @param startIndex relative address from which the new absolute base address should be used
   * @param baseAddress new absolute base address
   */
  public void rebase(int startIndex, int baseAddress)
  {
    Assert.isTrue(hasIndex(startIndex), "Precondition: hasIndex(startIndex)");
    assertValidAddress(baseAddress);

    Integer removed = startAddresses.put(startIndex, baseAddress);
    Assert.isNull(removed, "Precondition: Not rebased the same index twice");
  }

  /**
   * Change the absolute address of the code starting at the given index.
   *
   * @param startIndex relative address from which the new absolute address should be used
   * @param address new absolute address
   */
  public void base(int startIndex, int address)
  {
    Assert.isTrue(hasIndex(startIndex), "Precondition: hasIndex(startIndex)");
    assertValidAddress(address);

    Integer removed = startAddresses.put(startIndex, address - startIndex);
    Assert.isNull(removed, "Precondition: Not based the same index twice");
  }

  //
  // Subroutines
  //

  /**
   * Add a subroutine.
   *
   * @param index relative address of subroutine
   * @param numBytes number of argument bytes the subroutine expects after the JSR
   */
  public void addSubroutine(int index, int numBytes)
  {
    Assert.isTrue(hasIndex(index), "Precondition: hasIndex(index)");
    Assert.isTrue(numBytes > 0, "Precondition: numBytes > 0");

    subroutines.put(index, numBytes);
  }

  /**
   * Get number of argument bytes the subroutine at the given address expects.
   *
   * @param address absolute address
   * @return number of argument bytes or -1, if there is no subroutine at this address
   */
  public int getSubroutineArguments(int address) {
    if (!hasAddress(address)) {
      return -1;
    }

    Integer result = subroutines.get(indexForAddress(address));
    return result != null? result : -1;
  }

  /**
   * All subroutines.
   * Just for the mapper.
   */
  Map<Integer, Integer> getSubroutines()
  {
    return subroutines;
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
    Assert.isTrue(hasIndex(index), "Precondition: hasIndex(index)");

    return types[index];
  }

  /**
   * Set code type for the current opcode / command.
   *
   * @param type code type
   * @return whether a change has taken place
   */
  public boolean setType(CodeType type)
  {
    return setType(index, type);
  }

  /**
   * Set code type for a given relative address range.
   *
   * @param startIndex first relative address (incl.)
   * @param endIndex last relative address (excl.)
   * @param type code type
   * @return whether a change has taken place
   */
  public boolean setType(int startIndex, int endIndex, CodeType type)
  {
    Assert.isTrue(hasIndex(startIndex), "Precondition: hasIndex(startIndex)");
    Assert.isTrue(hasEndIndex(endIndex), "Precondition: hasEndIndex(endIndex)");
    Assert.isTrue(startIndex <= endIndex, "Precondition: startIndex <= endIndex");
    Assert.notNull(type, "Precondition: type != null");

    boolean change = false;
    for (int index = startIndex; index < endIndex; index++)
    {
      change|= setType(index, type);
    }

    return change;
  }

  /**
   * Set code type for a given relative address.
   *
   * @param index relative address
   * @param type code type
   * @return whether a change has taken place
   */
  public boolean setType(int index, CodeType type)
  {
    Assert.isTrue(hasIndex(index), "Precondition: hasIndex(index)");
    Assert.notNull(type, "Precondition: type != null");

    boolean change = !type.equals(types[index]);
    types[index] = type;

    return change;
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
    return hasLabel(current.getAddress());
  }

  /**
   * Is a label at the given absolute address?
   *
   * @param address absolute address
   */
  public boolean hasLabel(int address)
  {
    Assert.isTrue(hasAddress(address), "Precondition: hasAddress(address)");

    return hasCodeLabel(address) || hasDataLabel(address);
  }

  /**
   * Is there at least one code label pointing to the argument of the current opcode / command?
   */
  public boolean hasConflictingCodeLabel()
  {
    ICommand current = commands[index];
    for (int address = addressForIndex(index) + 1, count = 1; count < current.getSize(); address++, count++)
    {
      if (hasCodeLabel(address)) {
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
    ICommand current = commands[index];
    return index + current.getSize();
  }

  /**
   * Is the given relative address valid?.
   *
   * @param index relative address
   */
  public boolean hasIndex(int index)
  {
    return index >= 0 && index < code.length;
  }

  /**
   * Is the given relative end address valid?.
   *
   * @param index relative end address
   */
  public boolean hasEndIndex(int index)
  {
    return index >= 0 && index <= code.length;
  }

  /**
   * Compute absolute address from relative address.
   *
   * @param index relative address
   * @return absolute address
   */
  public int addressForIndex(int index)
  {
    Assert.isTrue(hasIndex(index), "Precondition: hasIndex(index)");

    // Compute start index for address range index belongs to
    Integer startIndex = startAddresses.floorKey(index);
    Assert.notNull(startIndex, "Check: lastStartIndex != null");

    return startAddresses.get(startIndex) + index;
  }

  /**
   * Is the given absolute address within the code?.
   *
   * @param address absolute address
   */
  public boolean hasAddress(int address)
  {
    return indexForAddressImpl(address) >= 0;
  }

  /**
   * Compute relative address from absolute address.
   *
   * @param address absolute address
   * @return relative address
   */
  public int indexForAddress(int address)
  {
    Assert.isTrue(hasAddress(address), "Precondition: hasAddress(address)");

    return indexForAddressImpl(address);
  }

  /**
   * Is the given absolute address within the code?.
   *
   * @param address absolute address
   * @return relative address or -1, if not found
   */
  private int indexForAddressImpl(int address)
  {
    Iterator<Entry<Integer, Integer>> iter = startAddresses.entrySet().iterator();
    Entry<Integer, Integer> lastAddressEntry = iter.next();
    while (iter.hasNext())
    {
      Entry<Integer, Integer> addressEntry = iter.next();
      if (address >= lastAddressEntry.getValue() + lastAddressEntry.getKey() &&
        address < lastAddressEntry.getValue() + addressEntry.getKey())
      {
        return address - lastAddressEntry.getValue();
      }
      lastAddressEntry = addressEntry;
    }

    return -1;
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
    Assert.isTrue(hasIndex(fromIndex), "Precondition: hasIndex(fromIndex)");

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
    Assert.isTrue(hasIndex(fromIndex), "Precondition: hasIndex(fromIndex)");
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
    Assert.isTrue(hasIndex(fromIndex), "Precondition: hasIndex(fromIndex)");
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
    Assert.isTrue(hasIndex(fromIndex), "Precondition: hasIndex(fromIndex)");
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
    int referenced = references[index];
    // delete reference
    references[index] = -1;

    if (referenced < 0)
    {
      // if nothing has been referenced, no label needs to be removed
      return false;
    }

    // check if referenced address is referenced from elsewhere too
    for (int reference : references)
    {
      if (reference == referenced)
      {
        // referenced address is still referenced, no label needs to be removed
        return false;
      }
    }

    // remove label, because the address is no more referenced
    Object removed = labels.remove(referenced);
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

    index = getNextIndex();
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
    return hasIndex(getNextIndex());
  }

  public ICommand nextCommand()
  {
    index = getNextIndex();
    ICommand result = commands[index];

    Assert.notNull(result, "Postcondition: result != null");
    return result;
  }

  public ICommand peekCommand() {
    int nextIndex = getNextIndex();
    return hasIndex(nextIndex)? commands[nextIndex] : DummyCommand.DUMMY_COMMAND;
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
    while (index > 0 && commands[--index] == null);
    ICommand result = commands[index];

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
  public void removeCurrentCommand()
  {
    // remember position of current command
    int remove = index;
    // skip current command
    index = getNextIndex();
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
    int endIndex = getNextIndex();

    // delete old command
    commands[index] = null;

    // add new commands
    for (ICommand replacement : replacements)
    {
      addCommand(index, replacement);
      index += replacement.getSize();
    }
    // TODO mh: trace back to the first replacement?!? comment!
    previousCommand();

    // check consistency
    Assert.isTrue(getNextIndex() == endIndex, "Precondition: The size of the replacements is equal to the size of the removed command");
  }
}

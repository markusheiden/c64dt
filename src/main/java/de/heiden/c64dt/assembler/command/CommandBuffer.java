package de.heiden.c64dt.assembler.command;

import static de.heiden.c64dt.assembler.command.DummyCommand.DUMMY_COMMAND;

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

import static de.heiden.c64dt.util.AddressUtil.assertValidAddress;

/**
 * Input stream for code.
 */
public class CommandBuffer {
  private final CodeType[] types;
  private final Integer[] codeReferences;
  private final Integer[] dataReferences;
  private final Integer[] externalReferences;
  private final Map<Integer, CodeLabel> codeLabels;
  private final Map<Integer, DataLabel> dataLabels;
  private final Map<Integer, ExternalLabel> externalLabels;
  private final SortedMap<Integer, Integer> startAddresses;
  private final LinkedList<ICommand> commands;
  private ListIterator<ICommand> iter;
  private ICommand current;
  private final int length;

  /**
   * Constructor.
   *
   * @param length length of code
   * @param startAddress address of the code
   */
  public CommandBuffer(int length, int startAddress) {
    Assert.isTrue(startAddress >= 0, "Precondition: startAddress >= 0");

    this.types = new CodeType[length];
    Arrays.fill(this.types, CodeType.UNKNOWN);
    this.codeReferences = new Integer[length];
    Arrays.fill(this.codeReferences, -1);
    this.dataReferences = new Integer[length];
    Arrays.fill(this.dataReferences, -1);
    this.externalReferences = new Integer[length];
    Arrays.fill(this.externalReferences, -1);

    this.codeLabels = new HashMap<Integer, CodeLabel>();
    this.dataLabels = new HashMap<Integer, DataLabel>();
    this.externalLabels = new HashMap<Integer, ExternalLabel>();
    this.startAddresses = new TreeMap<Integer, Integer>();
    this.commands = new LinkedList<ICommand>();
    this.iter = null;
    this.current = DUMMY_COMMAND;

    this.length = length;
    this.startAddresses.put(0, startAddress);
    this.startAddresses.put(length, startAddress + length);
  }

  /**
   * Is the given relative address valid?.
   *
   * @param index relative address
   */
  public boolean isValidIndex(int index) {
    return index >= 0 && index <= length;
  }

  /**
   * The current relative address.
   * This is the index where the next command will be added.
   */
  public int getCurrentIndex() {
    return current.getIndex() + current.getSize();
  }

  /**
   * Start address of code (incl.).
   */
  public int getStartAddress() {
    return startAddresses.get(0);
  }

  /**
   * Change the base address of the code starting at the given index.
   *
   * @param startIndex relative address from which the new absolute base address should be used
   * @param baseAddress new absolute base address
   */
  public void rebase(int startIndex, int baseAddress) {
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
  public CodeType getType() {
    return getType(current.getIndex());
  }

  /**
   * Get code type of the command at the given relative address.
   *
   * @param index relative address
   */
  public CodeType getType(int index) {
    Assert.isTrue(isValidIndex(index), "Precondition: isValidIndex(index)");

    return types[index];
  }

  /**
   * Set code type for the current opcode / command.
   *
   * @param type code type
   */
  public void setType(CodeType type) {
    setType(current.getIndex(), type);
  }

  /**
   * Set code type for a given relative address range.
   *
   * @param startIndex first relative address (incl.)
   * @param endIndex last relative address (excl.)
   * @param type code type
   */
  public void setType(int startIndex, int endIndex, CodeType type) {
    Assert.isTrue(isValidIndex(startIndex), "Precondition: isValidIndex(startIndex)");
    Assert.isTrue(isValidIndex(endIndex), "Precondition: isValidIndex(endIndex)");
    Assert.isTrue(startIndex <= endIndex, "Precondition: startIndex <= endIndex");
    Assert.notNull(type, "Precondition: type != null");

    for (int index = startIndex; index < endIndex; index++) {
      setType(index, type);
    }
  }

  /**
   * Set code type for a given relative address.
   *
   * @param index relative address
   * @param type code type
   */
  public void setType(int index, CodeType type) {
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
  public boolean hasLabel() {
    return getType().isCode()? hasCodeLabel(current.getAddress()) : hasDataLabel(current.getAddress());
  }

  /**
   * Is a code label at the current opcode / command?
   */
  public boolean hasCodeLabel() {
    return hasCodeLabel(current.getAddress());
  }

  /**
   * Is a code label at the given absolute address?
   *
   * @param address absolute address
   */
  public boolean hasCodeLabel(int address) {
    return codeLabels.containsKey(address);
  }

  /**
   * Is a data label at the current opcode / command?
   */
  public boolean hasDataLabel() {
    return hasDataLabel(current.getAddress());
  }

  /**
   * Is a data label at the given absolute address?
   *
   * @param address absolute address
   */
  public boolean hasDataLabel(int address) {
    return dataLabels.containsKey(address);
  }

  /**
   * Is the given absolute address within the code?.
   *
   * @param address absolute address
   */
  public boolean hasAddress(int address) {
    Iterator<Entry<Integer, Integer>> iter = startAddresses.entrySet().iterator();
    Entry<Integer, Integer> lastAddressEntry = iter.next();
    while (iter.hasNext()) {
      Entry<Integer, Integer> addressEntry = iter.next();
      if (address >= lastAddressEntry.getValue() + lastAddressEntry.getKey() &&
          address <  lastAddressEntry.getValue() + addressEntry.getKey()) {
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
  public int addressForIndex(int index) {
    Assert.isTrue(isValidIndex(index), "Precondition: isValidIndex(index)");

    int lastStartIndex = startAddresses.firstKey();
    for (int startIndex : startAddresses.keySet()) {
      if (index < startIndex) {
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
  public void addReference(boolean code, int fromIndex, int to) {
    Assert.isTrue(isValidIndex(fromIndex), "Precondition: isValidIndex(fromIndex)");

    if (!hasAddress(to)) {
      addExternalReference(fromIndex, to);
    } else if (code) {
      addCodeReference(fromIndex, to);
    } else {
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
  public void addCodeReference(int fromIndex, int to) {
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
  public void addDataReference(int fromIndex, int to) {
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
  public void addExternalReference(int fromIndex, int to) {
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
  public boolean removeReference() {
    int index = current.getIndex();

    Integer codeReference = codeReferences[index];
    Integer dataReference = dataReferences[index];
    Integer externalReference = externalReferences[index];

    codeReferences[index] = null;
    dataReferences[index] = null;
    externalReferences[index] = null;

    boolean codeFound = false;
    boolean dataFound = false;
    boolean externalFound = false;
    for (int i = 0; i < length; i++) {
      codeFound |= codeReference.equals(codeReferences[i]);
      dataFound |= dataReference.equals(dataReferences[i]);
      externalFound |= externalReference.equals(externalReferences[i]);
    }

    if (!codeFound) {
      codeLabels.remove(codeReference);
    }

    if (!dataFound) {
      dataLabels.remove(dataReference);
    }

    if (!externalFound) {
      externalLabels.remove(externalReference);
    }

    return !codeFound || !dataFound || !externalFound;
  }

  /**
   * Label representation for the current address.
   *
   * @return label representation or null if no label exists for the current address
   */
  public ILabel getLabel() {
    return getLabel(current.getAddress());
  }

  /**
   * Label representation for an absolute address.
   *
   * @param address absolute address
   * @return label or null if no label exists for this address
   */
  public ILabel getLabel(int address) {
    ILabel result = codeLabels.get(address);
    if (result == null) {
      result = dataLabels.get(address);
    }
    if (result == null) {
      result = externalLabels.get(address);
    }

    return result;
  }

  /**
   * All referenced addresses which do not point to this code.
   */
  public Collection<ExternalLabel> getExternalLabels() {
    return externalLabels.values();
  }

  //
  // command specific interface
  //

  /**
   * Clear all commands, references and labels.
   */
  public void clear() {
    Arrays.fill(codeReferences, -1);
    Arrays.fill(dataReferences, -1);
    Arrays.fill(externalReferences, -1);

    commands.clear();
    codeLabels.clear();
    dataLabels.clear();
    externalLabels.clear();
    iter = null;
    current = DUMMY_COMMAND;

  }

  /**
   * Add a command at the end of the buffer.
   *
   * @param command command
   */
  public void addCommand(ICommand command) {
    Assert.notNull(command, "Precondition: command != null");
    Assert.isTrue(!command.hasAddress(), "Precondition: !command.hasAddress()");

    int index = getCurrentIndex();

    Integer address = addressForIndex(index);
    Assert.isTrue(address >= 0, "Precondition: address >= 0");

    command.setAddress(index, address);
    commands.add(command);
    current = command;
    // TODO check / assert consistency; check that no iteration is in progress
  }

  //
  // Iterator
  //

  /**
   * (Re)start iteration.
   */
  public void restart() {
    iter = commands.listIterator();
    current = DUMMY_COMMAND;
  }

  public boolean hasNextCommand() {
    return iter.hasNext();
  }

  public ICommand nextCommand() {
    current = iter.next();

    Assert.notNull(current, "Postcondition: result != null");
    return current;
  }

  public boolean hasPreviousCommand() {
    return iter.hasPrevious();
  }

  public ICommand previousCommand() {
    current = iter.previous();

    Assert.notNull(current, "Postcondition: result != null");
    return current;
  }

  public void removeCurrentCommand() {
    iter.remove();
    // TODO check / assert consistency
  }

  /**
   * Replace the current command.
   *
   * @param replacements Replacement commands
   */
  public void replaceCurrentCommand(ICommand... replacements) {
    int index = current.getIndex();
    int address = current.getAddress();
    iter.remove();
    int size = 0;
    for (ICommand replacement : replacements) {
      replacement.setAddress(index + size, address + size);
      iter.add(replacement);
      size += replacement.getSize();
    }
    Assert.isTrue(current.getSize() == size, "Precondition: The size of the replacements is equal to the size of the removed command");
  }
}

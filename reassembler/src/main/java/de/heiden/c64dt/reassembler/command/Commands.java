package de.heiden.c64dt.reassembler.command;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.reassembler.label.CodeLabel;
import de.heiden.c64dt.reassembler.label.DataLabel;
import de.heiden.c64dt.reassembler.label.ExternalLabel;
import de.heiden.c64dt.reassembler.label.ILabel;

import static com.github.cowwoc.requirements10.java.DefaultJavaValidators.requireThat;
import static de.heiden.c64dt.bytes.AddressUtil.requireValidAddress;

/**
 * Input stream for code.
 */
public class Commands {
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
   * Commands as detected by the reassembler.
   */
  private final ICommand[] commands;

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
   * Absolute address to subroutines.
   */
  private final Map<Integer, Subroutine> subroutines;

  //
  //
  //

  /**
   * Constructor.
   *
   * @param code code
   * @param startAddress address of the code
   */
  public Commands(byte[] code, int startAddress) {
    requireThat(startAddress, "startAddress").isGreaterThanOrEqualTo(0);

    this.codeReferences = new int[code.length];
    Arrays.fill(this.codeReferences, -1);
    this.dataReferences = new int[code.length];
    Arrays.fill(this.dataReferences, -1);
    this.externalReferences = new int[code.length];
    Arrays.fill(this.externalReferences, -1);
    this.codeLabels = new HashMap<>();
    this.dataLabels = new HashMap<>();
    this.externalLabels = new HashMap<>();

    this.code = code;
    this.types = new CodeType[code.length];
    Arrays.fill(this.types, CodeType.UNKNOWN);
    this.startAddresses = new TreeMap<>();
    this.startAddresses.put(0, startAddress);
    this.startAddresses.put(code.length, startAddress);
    this.subroutines = new HashMap<>();
    this.commands = new ICommand[code.length];
  }

  /**
   * Hidden constructor for JAXB.
   */
  private Commands() {
    // TODO mh: init final fields with null instead
    this(new byte[0], 0);
  }

  //
  // code
  //

  /**
   * Code to be reassembled.
   */
  public byte[] getCode() {
    return code;
  }

  /**
   * Is the given relative address valid?.
   *
   * @param index relative address
   */
  public boolean hasIndex(int index) {
    return index >= 0 && index < code.length;
  }

  /**
   * Is the given relative end address valid?.
   *
   * @param index relative end address
   */
  public boolean hasEndIndex(int index) {
    return index >= 0 && index <= code.length;
  }

  /**
   * Length of code.
   */
  public int getLength() {
    return code.length;
  }

  //
  // base addresses
  //

  /**
   * Start address of code (incl.).
   */
  public int getStartAddress() {
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
   * Compute absolute address from relative address.
   *
   * @param index relative address
   * @return absolute address
   */
  public int addressForIndex(int index) {
    requireThat(hasIndex(index), "hasIndex(index)").isTrue();

    // Compute start index for address range index belongs to
    Integer startIndex = startAddresses.floorKey(index);
    requireThat(startIndex, "startIndex").isNotNull();

    return startAddresses.get(startIndex) + index;
  }

  /**
   * Is the given absolute address within the code?.
   *
   * @param address absolute address
   */
  public boolean hasAddress(int address) {
    return indexForAddressImpl(address) >= 0;
  }

  /**
   * Compute relative address from absolute address.
   *
   * @param address absolute address
   * @return relative address or -1, if not found
   */
  public int indexForAddress(int address) {
    requireThat(hasAddress(address), "hasAddress(address)").isTrue();

    return indexForAddressImpl(address);
  }

  /**
   * Is the given absolute address within the code?.
   *
   * @param address absolute address
   * @return relative address or -1, if not found
   */
  private int indexForAddressImpl(int address) {
    Iterator<Entry<Integer, Integer>> iter = startAddresses.entrySet().iterator();
    Entry<Integer, Integer> lastAddressEntry = iter.next();
    while (iter.hasNext()) {
      Entry<Integer, Integer> addressEntry = iter.next();
      if (address >= lastAddressEntry.getValue() + lastAddressEntry.getKey() &&
        address < lastAddressEntry.getValue() + addressEntry.getKey()) {
        return address - lastAddressEntry.getValue();
      }
      lastAddressEntry = addressEntry;
    }

    return -1;
  }

  /**
   * Change the absolute base address of the code starting at the given index.
   *
   * @param startIndex relative address from which the new absolute base address should be used
   * @param baseAddress new absolute base address
   */
  public void rebase(int startIndex, int baseAddress) {
    requireThat(hasIndex(startIndex), "hasIndex(startIndex)").isTrue();
    requireValidAddress(baseAddress);

    Integer removed = startAddresses.put(startIndex, baseAddress);
    // Check that the same index is not rebased twice.
    requireThat(removed, "removed").isNull();
  }

  /**
   * Change the absolute address of the code starting at the given index.
   *
   * @param startIndex relative address from which the new absolute address should be used
   * @param address new absolute address
   */
  public void base(int startIndex, int address) {
    requireThat(hasIndex(startIndex), "hasIndex(startIndex)").isTrue();
    requireValidAddress(address);

    Integer removed = startAddresses.put(startIndex, address - startIndex);
    // Check that the same index is not based twice.
    requireThat(removed, "removed").isNull();
  }

  //
  // Subroutines
  //

  /**
   * Add a subroutine.
   *
   * @param subroutine Subroutine
   */
  public void addSubroutine(Subroutine subroutine) {
    requireThat(subroutine, "subroutine").isNotNull();

    Subroutine removed = subroutines.put(subroutine.getAddress(), subroutine);
    // Check that there are no doubled subroutines.
    requireThat(removed, "removed").isNull();
  }

  /**
   * Get subroutine at a given absolute address.
   *
   * @param address absolute address
   * @return subroutine or null, if there is no subroutine at this address
   */
  public Subroutine getSubroutine(int address) {
    return subroutines.get(address);
  }

  /**
   * All subroutines.
   * Just for the mapper.
   */
  Map<Integer, Subroutine> getSubroutines() {
    return subroutines;
  }

  //
  // code type specific stuff ("model")
  //

  /**
   * Get code type of the command at the given relative address.
   *
   * @param index relative address
   */
  public CodeType getType(int index) {
    requireThat(hasIndex(index), "hasIndex(index)").isTrue();

    return types[index];
  }

  /**
   * Set code type for a given relative address range.
   *
   * @param startIndex first relative address (incl.)
   * @param endIndex last relative address (excl.)
   * @param type code type
   * @return whether a change has taken place
   */
  public boolean setType(int startIndex, int endIndex, CodeType type) {
    requireThat(hasIndex(startIndex), "hasIndex(startIndex)").isTrue();
    requireThat(hasEndIndex(endIndex), "hasEndIndex(endIndex)").isTrue();
    requireThat(startIndex, "startIndex").isLessThanOrEqualTo(endIndex, "endIndex");
    requireThat(type, "type").isNotNull();

    boolean change = false;
    for (int index = startIndex; index < endIndex; index++) {
      change |= setType(index, type);
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
  public boolean setType(int index, CodeType type) {
    requireThat(hasIndex(index), "hasIndex(index)").isTrue();
    requireThat(type, "type").isNotNull();

    boolean change = !type.equals(types[index]);
    types[index] = type;

    return change;
  }

  //
  // label/reference specific interface
  //

  /**
   * Is a label at the given absolute address?
   *
   * @param address absolute address
   */
  public boolean hasLabel(int address) {
    requireThat(hasAddress(address), "hasAddress(address)").isTrue();

    return hasCodeLabel(address) || hasDataLabel(address);
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
   * Is a code label at the given absolute address?
   *
   * @param address absolute address
   */
  public boolean hasCodeLabel(int address) {
    return codeLabels.containsKey(address);
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
   * All referenced addresses which do not point to this code.
   */
  public Collection<ExternalLabel> getExternalLabels() {
    return externalLabels.values();
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
    requireThat(hasIndex(fromIndex), "hasIndex(fromIndex)").isTrue();

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
    requireThat(hasIndex(fromIndex), "hasIndex(fromIndex)").isTrue();
    requireThat(hasAddress(to), "hasAddress(to)").isTrue();

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
    requireThat(hasIndex(fromIndex), "hasIndex(fromIndex)").isTrue();
    requireThat(hasAddress(to), "hasAddress(to)").isTrue();

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
    requireThat(hasIndex(fromIndex), "hasIndex(fromIndex)").isTrue();
    requireThat(hasAddress(to), "hasAddress(to)").isFalse();

    // add label for address "to"
    externalLabels.put(to, new ExternalLabel(to));
    // add reference
    externalReferences[fromIndex] = to;
  }

  /**
   * Get all references to an address.
   *
   * @param address Absolute address
   */
  public SortedSet<Integer> getReferences(int address) {
    SortedSet<Integer> result = new TreeSet<>();
    for (int i = 0; i < code.length; i++) {
      if (codeReferences[i] == address) {
        result.add(i);
      } else if (dataReferences[i] == address) {
        result.add(i);
      }
      // external references are not needed, because they do never point into the reassembled code
    }

    return result;
  }

  /**
   * Remove a reference from the given relative address.
   *
   * @param index relative address
   * @return whether a label has been removed
   */
  public boolean removeReference(int index) {
    return
      removeReference(index, codeReferences, codeLabels) |
        removeReference(index, dataReferences, dataLabels) |
        removeReference(index, externalReferences, externalLabels);
  }

  /**
   * Remove a reference from the given relative address
   *
   * @param index relative address
   * @param references all references
   * @param labels all labels
   * @return whether a label has been removed
   */
  private boolean removeReference(int index, int[] references, Map<Integer, ?> labels) {
    // get referenced absolute address
    int referenced = references[index];
    // delete reference
    references[index] = -1;

    if (referenced < 0) {
      // if nothing has been referenced, no label needs to be removed
      return false;
    }

    // check if referenced address is referenced from elsewhere too
    for (int reference : references) {
      if (reference == referenced) {
        // referenced address is still referenced, no label needs to be removed
        return false;
      }
    }

    // remove label, because the address is no more referenced
    Object removed = labels.remove(referenced);
    // There need to be a label if there had been a reference.
    requireThat(removed, "removed").isNotNull();

    // label has been removed
    return true;
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
    codeLabels.clear();
    dataLabels.clear();
    externalLabels.clear();
    Arrays.fill(commands, null);
  }

  /**
   * Get command at relative address.
   *
   * @param index relative address
   */
  public ICommand getCommand(int index) {
    requireThat(hasIndex(index), "hasIndex(index)").isTrue();

    return commands[index];
  }

  /**
   * Add a command at index.
   *
   * @param index relative address
   * @param command command
   */
  public void setCommand(int index, ICommand command) {
    requireThat(hasIndex(index), "hasIndex(index)").isTrue();
    requireThat(command, "command").isNotNull();
    requireThat(command.hasAddress(), "command.hasAddress()").isFalse();

    int address = addressForIndex(index);
    requireThat(address, "address").isGreaterThanOrEqualTo(0);

    command.setAddress(address);
    commands[index] = command;
    index += command.getSize();
  }

  /**
   * Remove the command from the given relative address.
   *
   * @param index relative address
   */
  void removeCommand(int index) {
    requireThat(hasIndex(index), "hasIndex(index)").isTrue();

    commands[index] = null;
  }
}

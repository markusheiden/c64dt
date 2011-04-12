package de.heiden.c64dt.assembler.command;

import de.heiden.c64dt.assembler.CodeLabel;
import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.assembler.DataLabel;
import de.heiden.c64dt.assembler.ExternalLabel;
import de.heiden.c64dt.assembler.ILabel;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;

import static de.heiden.c64dt.util.AddressUtil.assertValidAddress;

/**
 * Input stream for code.
 */
public class CommandBuffer {
  private final Map<Integer, CodeType> codeTypes;
  private final Map<Integer, CodeLabel> codeLabels;
  private final Map<Integer, DataLabel> dataLabels;
  private final Map<Integer, ExternalLabel> externalLabels;
  private final Map<Integer, Integer> codeReferences;
  private final Map<Integer, Integer> dataReferences;
  private final Map<Integer, Integer> externalReferences;
  private final LinkedList<ICommand> commands;
  private ListIterator<ICommand> iter;
  private ICommand current;
  private final int length;
  private final int startAddress;

  /**
   * Constructor.
   *
   * @param length legnth of code
   * @param startAddress address of the code
   */
  public CommandBuffer(int length, int startAddress) {
    Assert.isTrue(startAddress >= 0, "Precondition: startAddress >= 0");

    this.codeTypes = new HashMap<Integer, CodeType>();
    this.codeLabels = new HashMap<Integer, CodeLabel>();
    this.dataLabels = new HashMap<Integer, DataLabel>();
    this.externalLabels = new HashMap<Integer, ExternalLabel>();
    this.codeReferences = new HashMap<Integer, Integer>();
    this.dataReferences = new HashMap<Integer, Integer>();
    this.externalReferences = new HashMap<Integer, Integer>();
    this.commands = new LinkedList<ICommand>();
    this.iter = null;
    this.current = null;

    this.length = length;
    this.startAddress = startAddress;
  }

  /**
   * Start address of code (incl.).
   */
  public int getStartAddress() {
    return startAddress;
  }

  //
  // code type specific stuff ("model")
  //

  /**
   * Get code type of the current opcode / command.
   */
  public CodeType getType() {
    return getType(current.getAddress());
  }

  /**
   * Get code type of the command at the given address.
   *
   * @param address Address
   */
  public CodeType getType(int address) {
    CodeType result = codeTypes.get(address);
    return result != null? result : CodeType.UNKNOWN;
  }

  /**
   * Set code type for a given address.
   *
   * @param startAddress first address (incl.)
   * @param endAddress last address (excl.)
   * @param type code type
   */
  public void setType(int startAddress, int endAddress, CodeType type) {
    Assert.isTrue(startAddress <= endAddress, "Precondition: startAddress <= endAddress");
    Assert.notNull(type, "Precondition: type != null");

    for (int address = startAddress; address < endAddress; address++)
    {
      setType(address, type);
    }
  }

  /**
   * Set code type for the current opcode / command.
   *
   * @param type code type
   */
  public void setType(CodeType type) {
    setType(current.getAddress(), type);
  }

  /**
   * Set code type for a given address.
   *
   * @param address address
   * @param type code type
   */
  public void setType(int address, CodeType type) {
    Assert.notNull(type, "Precondition: type != null");

    codeTypes.put(address, type);
  }

  //
  // label/reference specific interface
  //

  /**
   * Is a label at the current opcode / command?
   */
  public boolean hasLabel() {
    CodeType type = getType();
    return type != CodeType.UNKNOWN && type.isCode()?
      hasCodeLabel(current.getAddress()) : hasDataLabel(current.getAddress());
  }

  /**
   * Is a code label at the current opcode / command?
   */
  public boolean hasCodeLabel() {
    return hasCodeLabel(current.getAddress());
  }

  /**
   * Is a code label at the given address?
   *
   * @param address address
   */
  public boolean hasCodeLabel(int address) {
    return codeLabels.containsKey(address);
  }

  /**
   * Has the given command a conflict with following code labels?
   *
   * @param address address of the command
   * @param lookAhead number of bytes to look ahead for a conflict
   */
  public boolean hasConflict(int address, int lookAhead) {
    if (hasCodeLabel(address)) {
      // if there is a code label for the command assume the command to be correct
      return false;
    }

    // otherwise look for code labels "into" the command
    for (int i = 1; i <= lookAhead; i++) {
      if (hasCodeLabel(address + i)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Is a code label at the current opcode / command?
   */
  public boolean hasDataLabel() {
    return hasDataLabel(current.getAddress());
  }

  /**
   * Is a code label at the given address?
   *
   * @param address address
   */
  public boolean hasDataLabel(int address) {
    return dataLabels.containsKey(address);
  }

  /**
   * Is the given address within the code?.
   *
   * @param address address
   */
  public boolean hasAddress(int address) {
    return startAddress <= address && address < startAddress + length;
  }

  /**
   * Add a reference from the current address to a given address.
   * This will add a label if no label exists for the given address.
   *
   * @param code is it a code reference?
   * @param from address of command referencing
   * @param to referenced address
   */
  public void addReference(boolean code, int from, int to) {
    Assert.isTrue(hasAddress(from), "Precondition: hasAddress(from)");

    if (!hasAddress(to)) {
      addExternalReference(from, to);
    } else if (code) {
      addCodeReference(from, to);
    } else {
      addDataReference(from, to);
    }
  }

  /**
   * Add a code reference from the current address to a given address.
   * This will add a label if no label exists for the given address.
   *
   * @param from address of command referencing
   * @param to referenced address
   */
  public void addCodeReference(int from, int to) {
    assertValidAddress(from);
    assertValidAddress(to);

    // add label for address "to"
    codeLabels.put(to, new CodeLabel(to));
    // add reference
    codeReferences.put(from, to);
  }

  /**
   * Add a data reference from the current address to a given address.
   * This will add a label if no label exists for the given address.
   *
   * @param from address of command referencing
   * @param to referenced address
   */
  public void addDataReference(int from, int to) {
    assertValidAddress(from);
    assertValidAddress(to);

    // add label for address "to"
    dataLabels.put(to, new DataLabel(to));
    // add reference
    dataReferences.put(from, to);
  }

  /**
   * Add an external code or data reference from the current address to a given address.
   * This will add a label if no label exists for the given address.
   *
   * @param from address of command referencing
   * @param to referenced address
   */
  public void addExternalReference(int from, int to) {
    assertValidAddress(from);
    assertValidAddress(to);

    // add label for address "to"
    externalLabels.put(to, new ExternalLabel(to));
    // add reference
    externalReferences.put(from, to);
  }

  /**
   * Remove a reference from the current address.
   *
   * @return whether a label has been removed
   */
  public boolean removeReference() {
    boolean result = false;

    int address = current.getAddress();

    Integer codeReference = codeReferences.remove(address);
    if (!codeReferences.containsValue(codeReference)) {
      codeLabels.remove(codeReference);
      result = true;
    }

    Integer dataReference = dataReferences.remove(address);
    if (!dataReferences.containsValue(dataReference)) {
      dataLabels.remove(dataReference);
      result = true;
    }

    Integer externalReference = externalReferences.remove(address);
    if (!externalReferences.containsValue(externalReference)) {
      externalLabels.remove(externalReference);
      result = true;
    }

    return result;
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
   * Label representation for an address.
   *
   * @param address address
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
    commands.clear();
    codeReferences.clear();
    codeLabels.clear();
    dataReferences.clear();
    dataLabels.clear();
    externalLabels.clear();
    iter = null;
    current = null;

  }

  /**
   * Add a command at the end of the buffer.
   *
   * @param command command
   */
  public void addCommand(ICommand command) {
    Assert.notNull(command, "Precondition: command != null");
    Assert.isTrue(!command.hasAddress(), "Precondition: !command.hasAddress()");

    command.setAddress(commands.isEmpty()? startAddress : commands.getLast().getNextAddress());
    commands.add(command);
    current = command;
    // TODO check / assert consistency; check that no iteration is in progress
  }

  public void restart() {
    iter = commands.listIterator();
    current = null;
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
    int address = current.getAddress();
    iter.remove();
    int size = 0;
    for (ICommand replacement : replacements) {
      replacement.setAddress(address + size);
      iter.add(replacement);
      size += replacement.getSize();
    }
    Assert.isTrue(current.getSize() == size, "Precondition: The size of the replacements is equal to the size of the removed command");
  }
}

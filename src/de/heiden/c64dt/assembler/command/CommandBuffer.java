package de.heiden.c64dt.assembler.command;

import de.heiden.c64dt.assembler.CodeLabel;
import de.heiden.c64dt.assembler.DataLabel;
import de.heiden.c64dt.assembler.ILabel;
import static de.heiden.c64dt.util.AddressUtil.assertValidAddress;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;

/**
 * Input stream for code.
 */
public class CommandBuffer {
  private final Map<Integer, CodeLabel> codeLabels;
  private final Map<Integer, DataLabel> dataLabels;
  private final Map<Integer, Integer> codeReferences;
  private final Map<Integer, Integer> dataReferences;
  private final LinkedList<ICommand> commands;
  private ListIterator<ICommand> iter;
  private ICommand current;
  private final int startAddress;

  /**
   * Constructor.
   *
   * @param startAddress address of the code
   */
  public CommandBuffer(int startAddress) {
    Assert.isTrue(startAddress >= 0, "Precondition: startAddress >= 0");

    this.codeLabels = new HashMap<Integer, CodeLabel>();
    this.dataLabels = new HashMap<Integer, DataLabel>();
    this.codeReferences = new HashMap<Integer, Integer>();
    this.dataReferences = new HashMap<Integer, Integer>();
    this.commands = new LinkedList<ICommand>();
    this.iter = null;
    this.current = null;
    this.startAddress = startAddress;
  }

  /**
   * Start address of code (incl.).
   */
  public int getStartAddress() {
    return startAddress;
  }

  //
  // label/reference specific interface
  //

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
   * Is a code label at the given address?
   *
   * @param address address
   */
  public boolean hasDataLabel(int address) {
    return dataLabels.containsKey(address);
  }

  /**
   * Add a reference from the current address to a given address.
   * This will add a label if no label exists for the given address.
   *
   * @param code is it a code reference?
   * @param address address
   */
  public void addReference(boolean code, int address) {
    if (code) {
      addCodeReference(address);
    } else {
      addDataReference(address);
    }
  }

  /**
   * Add a code reference from the current address to a given address.
   * This will add a label if no label exists for the given address.
   *
   * @param address address
   */
  public void addCodeReference(int address) {
    assertValidAddress(address);

    codeLabels.put(address, new CodeLabel(address));
    codeReferences.put(current.getAddress(), address);
  }

  /**
   * Add a data reference from the current address to a given address.
   * This will add a label if no label exists for the given address.
   *
   * @param address address
   */
  public void addDataReference(int address) {
    assertValidAddress(address);

    dataLabels.put(address, new DataLabel(address));
    dataReferences.put(current.getAddress(), address);
  }

  /**
   * Remove a reference from the current address.
   *
   * @return whether a code label before the current position has been removed due to reference removal
   */
  public boolean removeReference() {
    Integer removedDataLabel = dataReferences.remove(current.getAddress());
    if (removedDataLabel != null && !dataReferences.containsValue(removedDataLabel)) {
      dataLabels.remove(removedDataLabel);
    }

    Integer removedCodeLabel = codeReferences.remove(current.getAddress());
    if (removedCodeLabel != null && !codeReferences.containsValue(removedCodeLabel)) {
      codeLabels.remove(removedCodeLabel);
      return removedCodeLabel <= current.getAddress();
    }

    return false;
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

    return result;
  }

  //
  // command specific interface
  //

  /**
   * Clear all commands.
   */
  public void clear() {
    commands.clear();
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

  public void removeCurrentCommand() {
    iter.remove();
    // TODO check / assert consistency
  }
}

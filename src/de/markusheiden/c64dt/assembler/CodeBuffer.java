package de.markusheiden.c64dt.assembler;

import de.markusheiden.c64dt.util.ByteUtil;
import static de.markusheiden.c64dt.util.AddressUtil.assertValidAddress;
import static de.markusheiden.c64dt.util.HexUtil.format4;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

/**
 * Input stream for code.
 */
public class CodeBuffer {
  private int position;
  private int mark;
  private final byte[] code;
  private final Set<Integer> codeLabels;
  private final Set<Integer> dataLabels;
  private final Map<Integer, Integer> codeReferences;
  private final Map<Integer, Integer> dataReferences;
  private final ICommand[] commands;
  private final int startAddress;
  private final int endAddress;

  /**
   * Constructor.
   *
   * @param startAddress address of the code
   * @param code code
   */
  public CodeBuffer(int startAddress, byte[] code) {
    Assert.isTrue(startAddress >= 0, "Precondition: startAddress >= 0");
    Assert.notNull(code, "Precondition: code != null");

    this.position = 0;
    this.mark = -1;
    this.startAddress = startAddress;
    this.endAddress = startAddress + code.length;
    this.code = code;
    this.codeLabels = new HashSet<Integer>();
    this.dataLabels = new HashSet<Integer>();
    this.codeReferences = new HashMap<Integer, Integer>();
    this.dataReferences = new HashMap<Integer, Integer>();
    this.commands = new ICommand[code.length];
  }

  /**
   * Restart.
   * Sets the current position to the start of the code / commands.
   */
  public void restart() {
    position = 0;
    mark = -1;
  }

  /**
   * The current address.
   */
  public final int getAddress() {
    return address(position);
  }

  /**
   * Is the given address in the code?.
   */
  public final boolean hasAddress(int address) {
    return startAddress <= address && address <= endAddress;
  }

  /**
   * Start address of code (incl.).
   */
  public int getStartAddress() {
    return startAddress;
  }

  /**
   * End address of code (excl.).
   */
  public int getEndAddress() {
    return endAddress;
  }

  /**
   * Size of code.
   */
  public int getSize() {
    return endAddress - startAddress;
  }

  /**
   * Is the current address at the end of the code?.
   */
  public final boolean isEnd() {
    return position >= code.length;
  }

  /**
   * Are there 'number' bytes?
   *
   * @param number number of bytes
   */
  public final boolean has(int number) {
    Assert.isTrue(number >= 0, "Precondition: number >= 0");

    return position + number < code.length;
  }

  //
  // code specific interface
  //

  /**
   * Read an opcode.
   */
  public final Opcode readOpcode() {
    // remember position of last read opcode
    mark = position;
    return Opcode.opcode(readByte());
  }

  /**
   * Read 'number' bytes as one word.
   */
  public final int readRelative() {
    // read signed(!) offset
    return ((byte) readByte()) + getAddress();
  }

  /**
   * Read 'number' bytes as one word.
   *
   * @param number number of bytes to read
   */
  public final int readAbsolute(int number) {
    Assert.isTrue(number == 1 || number == 2, "Precondition: number == 1 || number == 2");

    return number == 1? readByte() : readByte() + (readByte() << 8);
  }

  //
  // label/reference specific interface
  //

  /**
   * Is a code label at the current opcode / command?
   */
  public boolean hasCodeLabel() {
    return hasCodeLabel(address(mark));
  }

  /**
   * Is a code label at the given address?
   *
   * @param address address
   */
  public boolean hasCodeLabel(int address) {
    return codeLabels.contains(address);
  }

  /**
   * Is a code label at the given address?
   *
   * @param address address
   */
  public boolean hasDataLabel(int address) {
    return dataLabels.contains(address);
  }

  /**
   * Add a reference from the current address to a given address.
   * This will add a label.
   *
   * @param code is this a reference to code?
   * @param address address
   */
  public void addReference(boolean code, int address) {
    assertValidAddress(address);

    if (code) {
      codeLabels.add(address);
      codeReferences.put(address(mark), address);
    } else {
      dataLabels.add(address);
      dataReferences.put(address(mark), address);
    }
  }

  /**
   * Remove a reference from the current address.
   *
   * @return whether a code label before the current position has been removed due to reference removal
   */
  public boolean removeReference() {
    Integer removedDataLabel = dataReferences.remove(address(mark));
    if (removedDataLabel != null && !dataReferences.containsValue(removedDataLabel)) {
      dataLabels.remove(removedDataLabel);
    }

    Integer removedCodeLabel = codeReferences.remove(address(mark));
    if (removedCodeLabel != null && !codeReferences.containsValue(removedCodeLabel)) {
      codeLabels.remove(removedCodeLabel);
      return removedCodeLabel <= address(mark);
    }

    return false;
  }

  /**
   * Label representation for the current address.
   *
   * @return label representation or null if no label exists for the current address
   */
  public String getLabel() {
    return getLabel(getAddress());
  }

  /**
   * Label representation for an address.
   *
   * @param address address
   * @return label representation or null if no label exists for this address
   */
  public String getLabel(int address) {
    if (hasCodeLabel(address)) {
      return "L" + format4(address);
    } else if (hasDataLabel(address)) {
      return "l" + format4(address);
    } else {
      return null;
    }
  }

  //
  // command specific interface
  //

  /**
   * Associate a command with the last read opcode / byte.
   *
   * @param command command
   */
  public void setCommand(ICommand command) {
    Assert.notNull(command, "Precondition: command != null");

    commands[mark] = command;
  }

  /**
   * Read a command and advance.
   * Advances the size of the command, when existing, or 1 otherwise.
   * @return the command at the current address or null, when no command has been associated with the current address
   */
  public final ICommand readCommand() {
    mark = position;
    ICommand result = commands[position];
    position += result == null? 1 : result.getSize();

    return result;
  }

  /**
   * Remove the current command.
   * Advances to the next command position.
   */
  public final void removeCommand() {
    position = mark + commands[mark].getSize();
    commands[mark] = null;
    mark = -1;
  }

  //
  // protected helper
  //

  /**
   * The address to an index.
   */
  protected final int address(int index) {
    Assert.isTrue(index >= 0 && index < code.length, "Precondition: index >= 0 && index < code.length");

    return startAddress + index;
  }

  /**
   * Read a byte from the code at the current position and advance.
   */
  protected int readByte() {
    return ByteUtil.toByte(code[position++]);
  }
}

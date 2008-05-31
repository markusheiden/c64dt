package de.markusheiden.c64dt.assembler;

import org.springframework.util.Assert;
import de.markusheiden.c64dt.util.ByteUtil;

/**
 * Input stream for code.
 */
public class CodeBuffer {
  private int position;
  private int opcodePosition;
  private final byte[] code;
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
    this.opcodePosition = -1;
    this.startAddress = startAddress;
    this.endAddress = startAddress + code.length;
    this.code = code;
    this.commands = new ICommand[code.length];
  }

  /**
   * Restart.
   * Sets the current position to the start of the code / commands.
   */
  public void restart() {
    position = 0;
    opcodePosition = -1;
  }

  /**
   * The current address.
   */
  public final int getAddress() {
    return startAddress + position;
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
    opcodePosition = position;
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
  // command specific interface
  //

  /**
   * Read a command and advance.
   * Advances the size of the command, when existing, or 1 otherwise.
   * @return the command at the current address or null, when no command has been associated with the current address
   */
  public final ICommand readCommand() {
    ICommand result = commands[position];
    position += result == null? 1 : result.getSize();
    return result;
  }

  /**
   * Associate a command with the last read opcode / byte.
   *
   * @param command command
   */
  public void setCommand(ICommand command) {
    Assert.notNull(command, "Precondition: command != null");

    commands[opcodePosition] = command;
  }

  //
  // protected interface
  //

  /**
   * Read a byte from the code at the current position and advance.
   */
  protected int readByte() {
    return ByteUtil.toByte(code[position++]);
  }
}

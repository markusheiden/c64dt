package main.java.de.heiden.c64dt.assembler;

import main.java.de.heiden.c64dt.util.ByteUtil;
import org.springframework.util.Assert;

import java.util.Arrays;

/**
 * Input stream for code.
 */
public class CodeBuffer {
  private int position;
  private int mark;
  private final byte[] code;
  private final CodeType[] types;
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
    this.types = new CodeType[code.length];
    Arrays.fill(this.types, CodeType.UNKNOWN);
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
   * The address of the current command.
   */
  public final int getCommandAddress() {
    return startAddress + mark;
  }

  /**
   * The current address.
   */
  public final int getCurrentAddress() {
    return startAddress + position;
  }

  /**
   * Is the given address in the code?.
   *
   * @param address address
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

  //
  // code specific interface
  //

  /**
   * Are there 'number' bytes left to read?
   *
   * @param number number of bytes
   */
  public final boolean has(int number) {
    Assert.isTrue(number >= 0, "Precondition: number >= 0");

    return position + number < code.length;
  }

  /**
   * Read a byte from the code at the current position and advance.
   */
  public final int readByte() {
    return ByteUtil.toByte(code[position++]);
  }

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
    return ((byte) readByte()) + getCurrentAddress();
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
  // code type specific stuff ("model")
  //

  /**
   * Get code type at current position.
   */
  public CodeType getType() {
    // TODO checks / contracts
    return types[position];
  }

  /**
   * Set code type for a given address.
   *
   * @param address address
   * @param type code type
   */
  public void setType(int address, CodeType type) {
    Assert.isTrue(hasAddress(address), "Precondition: hasAddress(address)");
    Assert.notNull(type, "Precondition: type != null");

    this.types[address - startAddress] = type;
  }
}

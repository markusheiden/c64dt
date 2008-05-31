package de.markusheiden.c64dt.assembler;

import org.springframework.util.Assert;

import java.io.ByteArrayInputStream;

/**
 * Input stream for code.
 */
public class CodeStream extends ByteArrayInputStream {
  private final int startAddress;
  private final int endAddress;

  /**
   * Constructor.
   *
   * @param address address of the code
   * @param code code
   */
  public CodeStream(int address, byte[] code) {
    super(code);

    Assert.isTrue(address >= 0, "Precondition: address >= 0");

    startAddress = address;
    endAddress = address + code.length;
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
   * Current position relative to the start of the code.
   */
  public int getPosition() {
    return pos;
  }

  /**
   * Are there 'number' bytes?
   *
   * @param number number of bytes
   */
  public final boolean has(int number) {
    Assert.isTrue(number >= 0, "Precondition: number >= 0");

    return pos + number < count;
  }

  /**
   * Read 'number' bytes as one word.
   */
  public final int readRelative() {
    // read signed(!) offset
    return ((byte) read()) + getAddress();
  }

  /**
   * Read 'number' bytes as one word.
   *
   * @param number number of bytes to read
   */
  public final int readAbsolute(int number) {
    Assert.isTrue(number == 1 || number == 2, "Precondition: number == 1 || number == 2");

    return number == 1? read() : read() + (read() << 8);
  }

  /**
   * The current address.
   */
  public final int getAddress() {
    return startAddress + pos;
  }

  /**
   * Is the given address in the code?.
   */
  public final boolean hasAddress(int address) {
    return startAddress <= address && address <= endAddress;
  }
}

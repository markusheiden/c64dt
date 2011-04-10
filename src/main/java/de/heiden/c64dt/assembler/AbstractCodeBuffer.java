package de.heiden.c64dt.assembler;

import de.heiden.c64dt.util.ByteUtil;
import org.springframework.util.Assert;

import java.util.Arrays;

/**
 * Abstract implementation of input stream for code.
 */
public abstract class AbstractCodeBuffer implements ICodeBuffer
{
  protected int position;
  private int mark;
  private final CodeType[] types;
  private final int startAddress;
  private final int endAddress;

  /**
   * Constructor.
   *
   * @param startAddress start address of the code
   * @param endAddress end address of the code
   */
  public AbstractCodeBuffer(int startAddress, int endAddress) {
    Assert.isTrue(startAddress >= 0, "Precondition: startAddress >= 0");
    Assert.isTrue(endAddress >= 0, "Precondition: endAddress >= 0");
    Assert.isTrue(startAddress < endAddress, "Precondition: < endAddress");

    this.position = 0;
    this.mark = -1;
    this.startAddress = startAddress;
    this.endAddress = endAddress;
    this.types = new CodeType[endAddress - startAddress];
    Arrays.fill(this.types, CodeType.UNKNOWN);
  }

  /**
   * Restart.
   * Sets the current position to the start of the code / commands.
   */
  @Override
  public void restart() {
    position = 0;
    mark = -1;
  }

  /**
   * The address of the current command.
   */
  @Override
  public final int getCommandAddress() {
    return startAddress + mark;
  }

  /**
   * The current address.
   */
  @Override
  public final int getCurrentAddress() {
    return startAddress + position;
  }

  /**
   * Is the given address in the code?.
   *
   * @param address address
   */
  @Override
  public final boolean hasAddress(int address) {
    return startAddress <= address && address <= endAddress;
  }

  /**
   * Start address of code (incl.).
   */
  @Override
  public int getStartAddress() {
    return startAddress;
  }

  /**
   * End address of code (excl.).
   */
  @Override
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
  @Override
  public final boolean has(int number) {
    Assert.isTrue(number >= 0, "Precondition: number >= 0");

    return startAddress + position + number <= endAddress;
  }

  /**
   * Read an opcode.
   */
  @Override
  public final Opcode readOpcode() {
    // remember position of last read opcode
    mark = position;
    return Opcode.opcode(readByte());
  }

  /**
   * Read 'number' bytes as one word.
   */
  @Override
  public final int readRelative() {
    // read signed(!) offset
    return ((byte) readByte()) + getCurrentAddress();
  }

  /**
   * Read 'number' bytes as one word.
   *
   * @param number number of bytes to read
   */
  @Override
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
  @Override
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
  @Override
  public void setType(int address, CodeType type) {
    Assert.isTrue(hasAddress(address), "Precondition: hasAddress(address)");
    Assert.notNull(type, "Precondition: type != null");

    this.types[address - startAddress] = type;
  }
}

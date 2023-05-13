package de.heiden.c64dt.assembler;

import static de.heiden.c64dt.common.Requirements.R;

/**
 * Abstract implementation of input stream for code.
 */
public abstract class AbstractCodeBuffer implements ICodeBuffer {
  private final int startAddress;
  private final int length;
  private int position;
  private int opcode;

  /**
   * Constructor.
   *
   * @param startAddress start address of the code
   * @param length length of the code
   */
  protected AbstractCodeBuffer(int startAddress, int length) {
    R.requireThat(length, "length").isGreaterThanOrEqualTo(0);

    this.startAddress = startAddress;
    this.length = length;
    this.position = 0;
    this.opcode = -1;
  }

  @Override
  public final int getCommandAddress() {
    return startAddress + getCommandIndex();
  }

  @Override
  public final int getCommandIndex() {
    return opcode;
  }

  @Override
  public final int getCurrentAddress() {
    return startAddress + getCurrentIndex();
  }

  @Override
  public void setCurrentAddress(int address) {
    setCurrentIndex(address - startAddress);
  }

  @Override
  public final int getCurrentIndex() {
    return position;
  }

  @Override
  public final void setCurrentIndex(int index) {
    position = index;
  }

  @Override
  public final boolean hasMore() {
    return position < length;
  }

  @Override
  public final boolean has(int number) {
    R.requireThat(number, "number").isGreaterThanOrEqualTo(0);

    return position + number <= length;
  }

  @Override
  public final Opcode readOpcode() {
    // remember position of last read opcode
    opcode = position;
    return Opcode.opcode(readByte());
  }

  @Override
  public final int read(int number) {
    R.requireThat(number, "number").isBetweenClosed(0, 2);

    if (number == 0) {
      return -1;
    }

    return number == 1 ? readByte() : readWord();
  }

  @Override
  public final int readByte() {
    return readByteAt(position++);
  }

  @Override
  public final int readWord() {
    return readByteAt(position++) + (readByteAt(position++) << 8);
  }

  /**
   * Read a byte from the source.
   *
   * @param index index of byte to read
   */
  protected abstract int readByteAt(int index);
}

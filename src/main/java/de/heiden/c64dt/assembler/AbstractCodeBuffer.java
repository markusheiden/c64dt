package de.heiden.c64dt.assembler;

import de.heiden.c64dt.util.ByteUtil;
import org.springframework.util.Assert;

/**
 * Abstract implementation of input stream for code.
 */
public abstract class AbstractCodeBuffer implements ICodeBuffer
{
  private int length;
  private int position;
  private int opcode;

  /**
   * Constructor.
   *
   * @param length length of the code
   */
  public AbstractCodeBuffer(int length)
  {
    Assert.isTrue(length >= 0, "Precondition: length >= 0");

    this.length = length;
    this.position = 0;
    this.opcode = -1;
  }

  @Override
  public final int getCommandIndex()
  {
    return opcode;
  }

  @Override
  public final int getCurrentIndex()
  {
    return position;
  }

  @Override
  public final void setCurrentIndex(int index)
  {
    position = index;
  }

  @Override
  public final boolean has(int number)
  {
    Assert.isTrue(number >= 0, "Precondition: number >= 0");

    return position + number <= length;
  }

  @Override
  public final Opcode readOpcode()
  {
    // remember position of last read opcode
    opcode = position;
    return Opcode.opcode(readByte());
  }

  @Override
  public final int read(int number)
  {
    Assert.isTrue(number == 1 || number == 2, "Precondition: number == 1 || number == 2");

    return number == 1? readByte() : readByte() + (readByte() << 8);
  }

  /**
   * Read a byte from the code at the current position and advance.
   */
  public final int readByte()
  {
    return readByteAt(position++);
  }

  /**
   * Read a byte from the source.
   *
   * @param index index of byte to read
   */
  protected abstract int readByteAt(int index);
}

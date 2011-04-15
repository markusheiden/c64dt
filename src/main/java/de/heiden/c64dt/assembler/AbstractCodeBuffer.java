package de.heiden.c64dt.assembler;

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

  /**
   * Constructor.
   *
   * @param length length of the code
   */
  public AbstractCodeBuffer(int length)
  {
    Assert.isTrue(length >= 0, "Precondition: length >= 0");

    this.mark = -1;
    this.position = 0;
    this.types = new CodeType[length];
    Arrays.fill(this.types, CodeType.UNKNOWN);
  }

  @Override
  public void restart()
  {
    position = 0;
    mark = -1;
  }

  @Override
  public final int getCommandIndex()
  {
    return mark;
  }

  @Override
  public final int getCurrentIndex()
  {
    return position;
  }

  @Override
  public final boolean has(int number)
  {
    Assert.isTrue(number >= 0, "Precondition: number >= 0");

    return position + number <= types.length;
  }

  @Override
  public final Opcode readOpcode()
  {
    // remember position of last read opcode
    mark = position;
    return Opcode.opcode(readByte());
  }

  @Override
  public final int read(int number)
  {
    Assert.isTrue(number == 1 || number == 2, "Precondition: number == 1 || number == 2");

    return number == 1 ? readByte() : readByte() + (readByte() << 8);
  }
}

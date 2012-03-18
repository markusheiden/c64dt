package de.heiden.c64dt.assembler;

import de.heiden.c64dt.util.ByteUtil;

/**
 * Input stream for code.
 */
public class CodeBuffer extends AbstractCodeBuffer {
  private final byte[] code;

  /**
   * Constructor.
   *
   * @param address start address of the code
   * @param code code
   */
  public CodeBuffer(int address, byte[] code) {
    super(address, code.length);

    this.code = code;
  }

  @Override
  protected int readByteAt(int index) {
    return ByteUtil.toByte(code[index]);
  }
}

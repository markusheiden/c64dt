package de.heiden.c64dt.assembler;

import de.heiden.c64dt.util.ByteUtil;
import org.springframework.util.Assert;

import java.util.Arrays;

/**
 * Input stream for code.
 */
public class CodeBuffer extends AbstractCodeBuffer {
  private final byte[] code;

  /**
   * Constructor.
   *
   * @param code code
   */
  public CodeBuffer(byte[] code) {
    super(code.length);

    this.code = code;
  }

  //
  // code specific interface
  //

  /**
   * Read a byte from the code at the current position and advance.
   */
  public final int readByte() {
    return ByteUtil.toByte(code[position++]);
  }
}

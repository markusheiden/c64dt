package de.heiden.c64dt.charset;

import java.nio.charset.Charset;
import java.nio.charset.UnmappableCharacterException;

/**
 * C64 charset with lower and upper case chars encoder.
 */
public class C64EncoderLower extends AbstractEncoder {
  /**
   * Constructor.
   */
  protected C64EncoderLower(Charset charset) {
    super(charset);
  }

  @Override
  protected byte toByte(char c) throws UnmappableCharacterException {
    if (c >= 'a' && c <= 'z') {
      return (byte) (c - 'a' + 0x01);
    } else if (c >= 'A' && c <= 'Z') {
      return (byte) (c - 'A' + 0x41);
    } else {
      return toSymbolByte(c);
    }
  }
}

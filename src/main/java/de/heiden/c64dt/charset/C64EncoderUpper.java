package de.heiden.c64dt.charset;

import java.nio.charset.Charset;
import java.nio.charset.UnmappableCharacterException;

/**
 * C64 charset with only upper case chars encoder.
 */
public class C64EncoderUpper extends AbstractEncoder {
  /**
   * Constructor.
   */
  protected C64EncoderUpper(Charset charset) {
    super(charset);
  }

  @Override
  protected byte toByte(char c) throws UnmappableCharacterException {
    if (c >= 'A' && c <= 'Z') {
      return (byte) (c - 'A' + 0x01);
    } else {
      return toSymbolByte(c);
    }
  }
}

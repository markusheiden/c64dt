package de.heiden.c64dt.charset;

import java.nio.charset.Charset;
import java.nio.charset.UnmappableCharacterException;

/**
 * C64 charset with only upper case chars decoder.
 */
public class C64DecoderUpper extends AbstractDecoder {
  /**
   * Constructor.
   */
  protected C64DecoderUpper(Charset charset) {
    super(charset);
  }

  @Override
  protected char toChar(byte b) throws UnmappableCharacterException {
    int c = b & 0x7F; // accept inverted chars too
    if (c >= 0x01 && c <= 0x1A) {
      return (char) (c - 0x01 + 'A');
    } else {
      return toSymbolChar(b);
    }
  }
}

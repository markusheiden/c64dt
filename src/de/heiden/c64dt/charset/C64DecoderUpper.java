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

  protected char toChar(byte b) throws UnmappableCharacterException {
    if (b >= 0x01 && b <= 0x1A) {
      return (char) (b - 0x01 + 'A');
    } else {
      return toSymbolChar(b);
    }
  }
}

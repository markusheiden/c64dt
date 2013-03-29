package de.heiden.c64dt.charset;

import java.nio.charset.Charset;
import java.nio.charset.UnmappableCharacterException;

/**
 * C64 charset with lower and upper case chars decoder.
 */
public class C64DecoderLower extends AbstractDecoder {
  /**
   * Constructor.
   */
  protected C64DecoderLower(Charset charset) {
    super(charset);
    this.replaceWith(" ");
  }

  @Override
  protected char toChar(byte b) throws UnmappableCharacterException {
    int c = b & 0x7F; // accept inverted chars too
    if (c >= 0x01 && c <= 0x1A) {
      return (char) (c - 0x01 + 'a');
    } else if (c >= 0x41 && c <= 0x5A) {
      return (char) (c - 0x41 + 'A');
    } else {
      return toSymbolChar(b);
    }
  }
}

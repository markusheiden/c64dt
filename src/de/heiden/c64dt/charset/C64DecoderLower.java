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

  protected char toChar(byte b) throws UnmappableCharacterException {
    if (b >= 0x01 && b <= 0x1A) {
      return (char) (b - 0x01 + 'a');
    } else if (b >= 0x41 && b <= 0x5A) {
      return (char) (b - 0x41 + 'A');
    } else {
      return toSymbolChar(b);
    }
  }
}

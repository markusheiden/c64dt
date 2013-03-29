package de.heiden.c64dt.charset;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.UnmappableCharacterException;

/**
 * Base class for simple (1 byte to 1 char) charset decoders.
 */
public abstract class AbstractDecoder extends CharsetDecoder {
  /**
   * Constructor.
   *
   * @param cs charset
   * @param replacement Replacement
   */
  protected AbstractDecoder(Charset cs, String replacement) {
    super(cs, 1, 1);
    replaceWith(replacement);
  }

  @Override
  protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
    try {
      while (in.hasRemaining() && out.hasRemaining()) {
        out.put(toChar(in.get()));
      }
      return in.hasRemaining() ? CoderResult.OVERFLOW : CoderResult.UNDERFLOW;
    } catch (UnmappableCharacterException e) {
      in.position(in.position() - 1);
      return CoderResult.unmappableForLength(1);
    }
  }

  /**
   * Convert a single byte to a char.
   *
   * @param b character byte
   */
  protected abstract char toChar(byte b) throws UnmappableCharacterException;
}

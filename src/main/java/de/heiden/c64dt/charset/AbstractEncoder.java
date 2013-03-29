package de.heiden.c64dt.charset;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.UnmappableCharacterException;

/**
 * Base class for simple (1 char to 1 byte) charset encoders.
 */
public abstract class AbstractEncoder extends CharsetEncoder {
  /**
   * Constructor.
   *
   * @param cs charset
   * @param replacement Replacement
   */
  protected AbstractEncoder(Charset cs, byte... replacement) {
    super(cs, 1, 1);
    replaceWith(replacement);
  }

  @Override
  protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
    try {
      while (in.hasRemaining() && out.hasRemaining()) {
        out.put(toByte(in.get()));
      }
      return in.hasRemaining() ? CoderResult.OVERFLOW : CoderResult.UNDERFLOW;
    } catch (UnmappableCharacterException e) {
      in.position(in.position() - 1);
      return CoderResult.unmappableForLength(1);
    }
  }

  /**
   * Convert a character to a single byte.
   *
   * @param c character
   */
  protected abstract byte toByte(char c) throws UnmappableCharacterException;
}

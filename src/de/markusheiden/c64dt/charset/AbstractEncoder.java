package de.markusheiden.c64dt.charset;

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
   */
  protected AbstractEncoder(Charset cs) {
    super(cs, 1, 1);
    replaceWith(new byte[]{0x20}); // space
  }

  protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
    try {
      while (in.hasRemaining() && out.hasRemaining()) {
        out.put(toByte(in.get()));
      }
      return in.hasRemaining()? CoderResult.OVERFLOW : CoderResult.UNDERFLOW;
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

  /**
   * Map common symbol characters.
   *
   * @param c symbol character
   */
  protected byte toSymbolByte(char c) throws UnmappableCharacterException {
    switch (c) {
      case '@': return 0x00;
      case '[': return 0x1B;
//      case ' ': return 0x1C; // pound
      case ']': return 0x1D;
//      case '^': return 0x1E; // up arrow
//      case '<': return 0x1F; // left arrow
      case ' ': return 0x20; // blank
      case '!': return 0x21;
      case '"': return 0x22;
      case '#': return 0x23;
      case '$': return 0x24;
      case '%': return 0x25;
      case '&': return 0x26;
      case '´': return 0x27;
      case '(': return 0x28;
      case ')': return 0x29;
      case '*': return 0x2A;
      case '+': return 0x2B;
      case ',': return 0x2C;
      case '-': return 0x2D;
      case '.': return 0x2E;
      case '/': return 0x2F;
      case '0': return 0x30;
      case '1': return 0x31;
      case '2': return 0x32;
      case '3': return 0x33;
      case '4': return 0x34;
      case '5': return 0x35;
      case '6': return 0x36;
      case '7': return 0x37;
      case '8': return 0x38;
      case '9': return 0x39;
      case ':': return 0x3A;
      case ';': return 0x3B;
      case '<': return 0x3C;
      case '=': return 0x3D;
      case '>': return 0x3E;
      case '?': return 0x3F;
//      case '-': return 0x40; // bold minus
//      case '+': return 0x5B; // bold plus
      case '_': return 0x64;
      default: throw new UnmappableCharacterException(1);
    }
  }
}

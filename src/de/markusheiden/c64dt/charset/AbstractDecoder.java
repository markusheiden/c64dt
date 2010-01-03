package de.markusheiden.c64dt.charset;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.UnmappableCharacterException;

/**
 * Base class for simple (1 byte to 1 char) charset decoders.
 */
public abstract class AbstractDecoder extends CharsetDecoder
{
  /**
   * Constructor.
   *
   * @param cs charset
   */
  protected AbstractDecoder(Charset cs)
  {
    super(cs, 1, 1);
    replaceWith(" ");
  }

  protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out)
  {
    try
    {
      while (in.hasRemaining() && out.hasRemaining())
      {
        // characters above 0x7F just have inverted colors
        out.put(toChar((byte) (in.get() & 0x7F)));
      }
      return in.hasRemaining() ? CoderResult.OVERFLOW : CoderResult.UNDERFLOW;
    }
    catch (UnmappableCharacterException e)
    {
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

  /**
   * Map common symbol characters.
   *
   * @param c Character
   */
  protected char toSymbolChar(byte c) throws UnmappableCharacterException
  {
    switch (c)
    {
      case 0x00:
        return '@';
      case 0x1B:
        return '[';
//      case 0x1C: return ' '; // pound
      case 0x1D:
        return ']';
//      case 0x1E: return '^'; // up arrow
//      case 0x1F: return '<'; // left arrow
      case 0x20:
        return ' '; // blank
      case 0x21:
        return '!';
      case 0x22:
        return '"';
      case 0x23:
        return '#';
      case 0x24:
        return '$';
      case 0x25:
        return '%';
      case 0x26:
        return '&';
      case 0x27:
        return '\'';
      case 0x28:
        return '(';
      case 0x29:
        return ')';
      case 0x2A:
        return '*';
      case 0x2B:
        return '+';
      case 0x2C:
        return ',';
      case 0x2D:
        return '-';
      case 0x2E:
        return '.';
      case 0x2F:
        return '/';
      case 0x30:
        return '0';
      case 0x31:
        return '1';
      case 0x32:
        return '2';
      case 0x33:
        return '3';
      case 0x34:
        return '4';
      case 0x35:
        return '5';
      case 0x36:
        return '6';
      case 0x37:
        return '7';
      case 0x38:
        return '8';
      case 0x39:
        return '9';
      case 0x3A:
        return ':';
      case 0x3B:
        return ';';
      case 0x3C:
        return '<';
      case 0x3D:
        return '=';
      case 0x3E:
        return '>';
      case 0x3F:
        return '?';
//      case 0x40: return '-'; // bold minus
//      case 0x5B: return '+'; // bold plus
      case 0x5D:
        return '|';
      case 0x64:
        return '_';
      default:
        throw new UnmappableCharacterException(1);
    }
  }
}

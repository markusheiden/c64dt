package de.heiden.c64dt.charset;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CoderResult;
import java.nio.charset.UnmappableCharacterException;

/**
 * Decoder for C64 ASCII "PetSCII".
 */
public class PetSCIIDecoder extends AbstractDecoder {
  /**
   * Charset.
   */
  private final PetSCIICharset charset;

  /**
   * Constructor.
   */
  protected PetSCIIDecoder(PetSCIICharset charset) {
    super(charset, " ");
    this.charset = charset;
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
   * Decode a single char.
   *
   * @param b Encode char
   */
  protected char toChar(byte b) throws UnmappableCharacterException {
    int c = b & 0xFF;
    if (c >= 0x41 && c <= 0x5A) {
      char a = charset.upper ? 'A' : 'a';
      return (char) (c - 0x41 + a);
    } else if (c >= 0x61 && c <= 0x7A) {
      if (charset.upper) {
        throw new UnmappableCharacterException(1);
      }
      return (char) (c - 0x61 + 'A');
    } else if (c >= 0xC1 && c <= 0xDA) {
      if (charset.upper) {
        throw new UnmappableCharacterException(1);
      }
      return (char) (c - 0xC1 + 'A');
    } else {
      switch (c) {
        case 0x0a:
          return '\r';
        case 0x0d:
          return '\n';
        case 0x0e:
          charset.upper = false;
          throw new UnmappableCharacterException(1);
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
        case 0x40:
          return '@';
        case 0x5B:
          return '[';
        case 0x5D:
          return ']';
        case 0x8E:
          charset.upper = true;
          throw new UnmappableCharacterException(1);
        case 0xA0:
          return ' ';
        default:
          // System.out.println(">" + Integer.toHexString(c) + "<");
          throw new UnmappableCharacterException(1);
      }
    }
  }
}

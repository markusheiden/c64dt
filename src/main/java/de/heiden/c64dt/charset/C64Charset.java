package de.heiden.c64dt.charset;

import java.nio.charset.Charset;
import java.nio.charset.UnmappableCharacterException;

/**
 * C64 charset.
 */
public class C64Charset extends AbstractCharset {
  /**
   * Default charset with only upper case chars.
   */
  public static final C64Charset UPPER = new C64Charset(true);

  /**
   * Alternate charset with lower and upper case chars.
   */
  public static final C64Charset LOWER = new C64Charset(false);

  /**
   * Upper and lower case charset?.
   */
  private final boolean upper;

  /**
   * Constructor.
   */
  protected C64Charset(boolean upper) {
    super("C64_" + (upper ? "UPPER" : "LOWER"));

    this.upper = upper;
  }

  public static C64Charset charset(boolean upper) {
    return upper ? UPPER : LOWER;
  }

  @Override
  public boolean contains(Charset cs) {
    return cs instanceof C64Charset &&
      !upper || ((C64Charset) cs).upper;
  }

  @Override
  public AbstractDecoder newDecoder() {
    return new AbstractDecoder(this, " ") {
      @Override
      protected char toChar(byte b) throws UnmappableCharacterException {
        int c = b & 0x7F; // accept inverted chars too
        if (c >= 0x01 && c <= 0x1A) {
          char a = upper ? 'A' : 'a';
          return (char) (c - 0x01 + a);
        } else if (!upper && c >= 0x41 && c <= 0x5A) {
          return (char) (c - 0x41 + 'A');
        } else {
          return toSymbolChar(b);
        }
      }
    };
  }

  /**
   * Map common symbol characters.
   *
   * @param c Character
   */
  protected char toSymbolChar(byte c) throws UnmappableCharacterException {
    switch (c) {
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

  @Override
  public AbstractEncoder newEncoder() {
    return new AbstractEncoder(this, (byte) 0x20) {
      @Override
      protected byte toByte(char c) throws UnmappableCharacterException {
        if (!upper && c >= 'a' && c <= 'z') {
          return (byte) (c - 'a' + 0x01);
        } else if (c >= 'A' && c <= 'Z') {
          int a = upper ? 0x01 : 0x41;
          return (byte) (c - 'A' + a);
        } else {
          return toSymbolByte(c);
        }
      }
    };
  }

  /**
   * Map common symbol characters.
   *
   * @param c symbol character
   */
  protected byte toSymbolByte(char c) throws UnmappableCharacterException {
    switch (c) {
      case '@':
        return 0x00;
      case '[':
        return 0x1B;
//      case ' ': return 0x1C; // pound
      case ']':
        return 0x1D;
//      case '^': return 0x1E; // up arrow
//      case '<': return 0x1F; // left arrow
      case ' ':
        return 0x20; // blank
      case '!':
        return 0x21;
      case '"':
        return 0x22;
      case '#':
        return 0x23;
      case '$':
        return 0x24;
      case '%':
        return 0x25;
      case '&':
        return 0x26;
      case '\'':
        return 0x27;
      case '(':
        return 0x28;
      case ')':
        return 0x29;
      case '*':
        return 0x2A;
      case '+':
        return 0x2B;
      case ',':
        return 0x2C;
      case '-':
        return 0x2D;
      case '.':
        return 0x2E;
      case '/':
        return 0x2F;
      case '0':
        return 0x30;
      case '1':
        return 0x31;
      case '2':
        return 0x32;
      case '3':
        return 0x33;
      case '4':
        return 0x34;
      case '5':
        return 0x35;
      case '6':
        return 0x36;
      case '7':
        return 0x37;
      case '8':
        return 0x38;
      case '9':
        return 0x39;
      case ':':
        return 0x3A;
      case ';':
        return 0x3B;
      case '<':
        return 0x3C;
      case '=':
        return 0x3D;
      case '>':
        return 0x3E;
      case '?':
        return 0x3F;
//      case '-': return 0x40; // bold minus
//      case '+': return 0x5B; // bold plus
      case '|':
        return 0x5D;
      case '_':
        return 0x64;
      default:
        throw new UnmappableCharacterException(1);
    }
  }
}

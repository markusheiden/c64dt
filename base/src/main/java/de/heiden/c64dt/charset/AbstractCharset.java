package de.heiden.c64dt.charset;

import org.springframework.util.Assert;

import java.nio.charset.Charset;
import java.nio.charset.UnmappableCharacterException;

/**
 * Charset with some convenience methods.
 */
public abstract class AbstractCharset extends Charset {
  /**
   * Constructor.
   *
   * @param canonicalName
   * @param aliases
   */
  protected AbstractCharset(String canonicalName, String... aliases) {
    super(canonicalName, aliases);
  }

  @Override
  public abstract AbstractDecoder newDecoder();

  /**
   * Convenience method to convert C64 encoded bytes into a string.
   *
   * @param bytes bytes
   */
  public String toString(byte... bytes) {
    Assert.notNull(bytes, "Precondition: bytes != null");

    return toString(bytes, 0, bytes.length);
  }

  /**
   * Convenience method to convert C64 encoded bytes into a string.
   *
   * @param bytes bytes
   * @param pos position in bytes to start from
   * @param length number of bytes to convert
   */
  public String toString(byte[] bytes, int pos, int length) {
    Assert.notNull(bytes, "Precondition: bytes != null");
    Assert.isTrue(pos >= 0, "Precondition: pos >= 0");
    Assert.isTrue(length >= 0, "Precondition: length >= 0");
    Assert.isTrue(pos + length <= bytes.length, "Precondition: pos + length <= bytes.length");

    AbstractDecoder decoder = newDecoder();
    StringBuilder result = new StringBuilder(bytes.length);
    for (int i = 0; i < length; i++, pos++) {
      char c = toChar(bytes[pos], decoder);
      if (c > 0) {
        result.append(c);
      }
    }

    return result.toString();
  }

  /**
   * Convenience method to convert C64 encoded byte into a char.
   *
   * @param b Byte
   * @return Decoded char or 0
   */
  public char toChar(byte b) {
    return toChar(b, newDecoder());
  }

  /**
   * Convenience method to convert C64 encoded byte into a char.
   *
   * @param b Byte
   * @param decoder Decoder
   * @return Decoded char or 0
   */
  private char toChar(byte b, AbstractDecoder decoder) {
    try {
      return decoder.toChar(b);
    } catch (UnmappableCharacterException e) {
      return 0;
    }
  }

  @Override
  public abstract AbstractEncoder newEncoder();

  /**
   * Convenience method to convert a string into C64 encoded bytes.
   *
   * @param string string
   */
  public byte[] toBytes(String string) {
    Assert.notNull(string, "Precondition: string != null");

    byte[] result = new byte[string.length()];
    toBytes(string, result, 0);

    Assert.notNull(result, "Postcondition: result != null");
    return result;
  }

  /**
   * Convenience method to convert a string into C64 encoded bytes.
   *
   * @param string string
   * @param bytes resulting bytes
   * @param pos position in bytes to write result to
   */
  public void toBytes(String string, byte[] bytes, int pos) {
    Assert.notNull(string, "Precondition: string != null");
    Assert.notNull(bytes, "Precondition: bytes != null");
    Assert.isTrue(pos >= 0, "Precondition: pos >= 0");
    Assert.isTrue(pos + string.length() <= bytes.length, "Precondition: pos + string.length() <= bytes.length");

    AbstractEncoder encoder = newEncoder();
    for (int i = 0; i < string.length(); i++, pos++) {
      bytes[pos] = toByte(string.charAt(i), encoder);
    }
  }

  /**
   * Convenience method to convert a char into C64 encoded byte.
   *
   * @param c Character
   */
  public byte toByte(char c) {
    return toByte(c, newEncoder());
  }

  /**
   * Convenience method to convert a char into C64 encoded byte.
   *
   * @param c Character
   * @param encoder Encoder
   */
  private byte toByte(char c, AbstractEncoder encoder) {
    try {
      return encoder.toByte(c);
    } catch (UnmappableCharacterException e) {
      return 0x20; // space
    }
  }
}

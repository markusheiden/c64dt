package de.heiden.c64dt.charset;

import java.nio.charset.Charset;
import java.nio.charset.UnmappableCharacterException;

import static de.heiden.c64dt.charset.Requirements.R;

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
    R.requireThat(bytes, "bytes").isNotNull();

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
    R.requireThat(bytes, "bytes").isNotNull();
    R.requireThat(pos, "pos").isGreaterThanOrEqualTo(0);
    R.requireThat(length, "length").isGreaterThanOrEqualTo(0);
    R.requireThat(pos + length, "pos + length").isLessThanOrEqualTo(bytes.length, "bytes.length");

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
    R.requireThat(string, "string").isNotNull();

    byte[] result = new byte[string.length()];
    toBytes(string, result, 0);

    R.requireThat(result, "result").isNotNull();
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
    R.requireThat(string, "string").isNotNull();
    R.requireThat(bytes, "bytes").isNotNull();
    R.requireThat(pos, "pos").isGreaterThanOrEqualTo(0);
    R.requireThat(pos + string.length(), "pos + string.length()").isLessThanOrEqualTo(bytes.length, "bytes.length");

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

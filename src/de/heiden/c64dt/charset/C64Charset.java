package de.heiden.c64dt.charset;

import org.springframework.util.Assert;

import java.nio.charset.Charset;
import java.nio.charset.UnmappableCharacterException;

/**
 * C64 charset.
 */
public class C64Charset extends Charset {
  /**
   * Default charset with only upper case chars.
   */
  public static final C64Charset UPPER = new C64Charset(true);

  /**
   * Alternate charset with lower and upper case chars.
   */
  public static final C64Charset LOWER = new C64Charset(false);

  private final boolean upper;

  /**
   * Constructor.
   */
  protected C64Charset(boolean upper) {
    super("C64_" + (upper? "UPPER" : "LOWER"), null);

    this.upper = upper;
  }

  public static C64Charset charset(boolean upper) {
    return upper? UPPER : LOWER;
  }

  public boolean contains(Charset cs) {
    return cs instanceof C64Charset &&
      !upper || ((C64Charset) cs).upper;
  }

  public AbstractDecoder newDecoder() {
    return upper? new C64DecoderUpper(this) : new C64DecoderLower(this);
  }

  public AbstractEncoder newEncoder() {
    return upper? new C64EncoderUpper(this) : new C64EncoderLower(this);
  }

  //
  // convenience methods
  //

  /**
   * Convenience method to convert C64 encoded bytes into a string.
   *
   * @param bytes bytes
   */
  public String toString(byte[] bytes) {
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
      try {
        result.append(decoder.toChar(bytes[pos]));
      } catch (UnmappableCharacterException e) {
        result.append(' ');
      }
    }

    return result.toString();
  }

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
      try {
        bytes[pos] = encoder.toByte(string.charAt(i));
      } catch (UnmappableCharacterException e) {
        bytes[pos] = 0x20;
      }
    }
  }
}

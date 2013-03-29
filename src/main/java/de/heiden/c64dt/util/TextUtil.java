package de.heiden.c64dt.util;

import org.springframework.util.Assert;

/**
 * Util class for handling C64 encoded text.
 */
public class TextUtil {
  /**
   * Strip trailing 0xA0 from file names etc.
   *
   * @param text text in C64 encoding
   */
  public static byte[] strip(byte[] text) {
    return strip(text, 0, text.length);
  }

  /**
   * Strip trailing 0xA0 from file names etc.
   *
   * @param text text in C64 encoding
   * @param pos
   * @param length
   */
  public static byte[] strip(byte[] text, int pos, int length) {
    Assert.notNull(text, "Precondition: text != null");
    Assert.isTrue(pos >= 0, "Precondition: pos >= 0");
    Assert.isTrue(length >= 0, "Precondition: length >= 0");
    Assert.isTrue(pos + length <= text.length, "Precondition: pos + length <= text.length");

    for (int i = pos + length - 1; i >= pos && text[i] == (byte) 0xA0; --i) {
      --length;
    }
    byte[] result = new byte[length];
    System.arraycopy(text, pos, result, 0, length);

    Assert.notNull(result, "Postcondition: result != null");
    return result;
  }
}

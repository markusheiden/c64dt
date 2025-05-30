package de.heiden.c64dt.charset;

import static com.github.cowwoc.requirements10.java.DefaultJavaValidators.requireThat;

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
   * @param text Text buffer in C64 encoding
   * @param pos Start index of text
   * @param length Length of text
   */
  public static byte[] strip(byte[] text, int pos, int length) {
    requireThat(text, "text").isNotNull();
    requireThat(pos, "pos").isGreaterThanOrEqualTo(0);
    requireThat(length, "length").isGreaterThanOrEqualTo(0);
    requireThat(pos + length, "pos + length").isLessThanOrEqualTo(text.length, "text.length");

    for (int i = pos + length - 1; i >= pos && text[i] == (byte) 0xA0; --i) {
      --length;
    }
    byte[] result = new byte[length];
    System.arraycopy(text, pos, result, 0, length);

    requireThat(result, "result").isNotNull();
    return result;
  }
}

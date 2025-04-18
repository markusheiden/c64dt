package de.heiden.c64dt.bytes;

import static com.github.cowwoc.requirements10.java.DefaultJavaValidators.requireThat;

/**
 * Helper class for handling hexadecimal values.
 */
public class HexUtil {
  /**
   * String representation of a byte.
   *
   * @require value < 0x100
   */
  public static String hexBytePlain(int value) {
    requireThat(value, "value").isBetween(0, 0xFF + 1);

    return Long.toHexString(value + 0x100).toUpperCase().substring(1);
  }

  /**
   * String representation of a byte with leading '$'.
   *
   * @require value < 0x100
   */
  public static String hexByte(int value) {
    return "$" + hexBytePlain(value);
  }

  /**
   * String representation of a word.
   *
   * @require value < 0x10000
   */
  public static String hexWordPlain(int value) {
    requireThat(value, "value").isBetween(0, 0xFFFF + 1);

    return Long.toHexString(value + 0x10000).toUpperCase().substring(1);
  }

  /**
   * String representation of a word with leading '$'.
   *
   * @require value < 0x10000
   */
  public static String hexWord(int value) {
    return "$" + hexWordPlain(value);
  }

  /**
   * String representation of a byte or a word with leading '$'.
   *
   * @require value < 0x10000
   */
  public static String hexPlain(int value) {
    requireThat(value, "value").isBetween(0, 0xFFFF + 1);

    return value < 0x100 ? hexBytePlain(value) : hexWordPlain(value);
  }

  /**
   * String representation of a byte or a word with leading '$'.
   *
   * @require value < 0x10000
   */
  public static String hex(int value) {
    return "$" + hexPlain(value);
  }

  /**
   * Parse string representation of a hex number.
   *
   * @param value String representation
   */
  public static int parseHexBytePlain(String value) {
    int result = Integer.parseInt(value, 16);

    requireThat(result, "result").isBetween(0, 0xFF + 1);
    return result;
  }

  /**
   * Parse string representation of a hex number.
   *
   * @param value String representation
   */
  public static int parseHexWordPlain(String value) {
    int result = Integer.parseInt(value, 16);

    requireThat(result, "result").isBetween(0, 0xFFFF + 1);
    return result;
  }
}

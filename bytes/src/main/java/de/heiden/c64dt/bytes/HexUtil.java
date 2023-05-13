package de.heiden.c64dt.bytes;

import static de.heiden.c64dt.common.Requirements.R;

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
    R.requireThat(value, "value").isBetweenClosed(0, 0xFF);

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
    R.requireThat(value, "value").isBetweenClosed(0, 0xFFFF);

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
    R.requireThat(value, "value").isBetweenClosed(0, 0xFFFF);

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

    R.requireThat(result, "result").isBetweenClosed(0, 0xFF);
    return result;
  }

  /**
   * Parse string representation of a hex number.
   *
   * @param value String representation
   */
  public static int parseHexWordPlain(String value) {
    int result = Integer.parseInt(value, 16);

    R.requireThat(result, "result").isBetweenClosed(0, 0xFFFF);
    return result;
  }
}

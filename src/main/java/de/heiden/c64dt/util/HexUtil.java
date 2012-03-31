package de.heiden.c64dt.util;

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
    assert value >= 0 : "Precondition: value >= 0";
    assert value < 0x100 : "Precondition: value < 0x100";

    String result = Long.toHexString(value + 0x100).toUpperCase();
    return result.substring(1);
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
    assert value >= 0 : "Precondition: value >= 0";
    assert value < 0x10000 : "value < 0x10000";

    String result = Long.toHexString(value + 0x10000).toUpperCase();
    return result.substring(1);
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
    assert value >= 0 : "Precondition: value >= 0";
    assert value < 0x10000 : "value < 0x10000";

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

    assert result >= 0 && result < 0x100 : "Precondition: result >= 0 && result < 0x100";
    return result;
  }

  /**
   * Parse string representation of a hex number.
   *
   * @param value String representation
   */
  public static int parseHexWordPlain(String value) {
    int result = Integer.parseInt(value, 16);

    assert result >= 0 && result < 0x10000 : "Precondition: result >= 0 && result < 0x10000";
    return result;
  }
}

package de.heiden.c64dt.util;

import static de.heiden.c64dt.util.AddressUtil.*;

/**
 * Helper class for handling hexadecimal values.
 */
public class HexUtil {
  /**
   * String representation of a byte.
   *
   * @require value < 0x100
   */
  public static String hexBytePlain (int value)
  {
    assert value >= 0: "Precondition: value >= 0";
    assert value < 0x100: "Precondition: value < 0x100";

    StringBuilder result = new StringBuilder(Long.toHexString(value + 0x100).toUpperCase());
    return result.substring(1);
  }

  /**
   * String representation of a byte with leading '$'.
   *
   * @require value < 0x100
   */
  public static String hexByte (int value)
  {
    assert value >= 0: "Precondition: value >= 0";
    assert value < 0x100: "Precondition: value < 0x100";

    StringBuilder result = new StringBuilder(Long.toHexString(value + 0x100).toUpperCase());
    result.setCharAt(0, '$');
    return result.toString();
  }

  /**
   * String representation of a word.
   *
   * @require value < 0x10000
   */
  public static String hexWordPlain (int value)
  {
    assert value >= 0: "Precondition: value >= 0";
    assert value < 0x10000: "value < 0x10000";

    StringBuilder result = new StringBuilder(Long.toHexString(value + 0x10000).toUpperCase());
    return result.substring(1);
  }

  /**
   * String representation of a word with leading '$'.
   *
   * @require value < 0x10000
   */
  public static String hexWord (int value)
  {
    assert value >= 0: "Precondition: value >= 0";
    assert value < 0x10000: "value < 0x10000";

    StringBuilder result = new StringBuilder(Long.toHexString(value + 0x10000).toUpperCase());
    result.setCharAt(0, '$');
    return result.toString();
  }

  /**
   * String representation of a byte or a word with leading '$'.
   *
   * @require value < 0x10000
   */
  public static String hexPlain (int value)
  {
    assert value >= 0: "Precondition: value >= 0";
    assert value < 0x10000: "value < 0x10000";

    return value < 0x100? hexBytePlain(value) : hexWordPlain(value);
  }

  /**
   * String representation of a byte or a word with leading '$'.
   *
   * @require value < 0x10000
   */
  public static String hex (int value)
  {
    assert value >= 0: "Precondition: value >= 0";
    assert value < 0x10000: "value < 0x10000";

    return value < 0x100? hexByte(value) : hexWord(value);
  }
}

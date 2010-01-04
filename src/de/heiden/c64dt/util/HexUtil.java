package de.heiden.c64dt.util;

/**
 * Helper class for handling hexadecimal values.
 */
public class HexUtil {
  private static final String[] ZEROS_2 = {
    "00" , "0"
  };

  private static final String[] ZEROS_4 = {
    "0000", "000", "00" , "0"
  };

  /**
   * Format max. 2 digit number hexadecimal.
   *
   * @param number number
   * @return
   */
  public static String format2(int number) {
    String result = Integer.toHexString(number).toUpperCase();
    final int length = result.length();
    if (length == 2) {
      return result;
    } else if (result.length() < 2) {
      return ZEROS_2[length] + result;
    } else {
      return result.substring(length - 2);
    }
  }

  /**
   * Format max. 4 digit number hexadecimal.
   *
   * @param number number
   * @return
   */
  public static String format4(int number) {
    String result = Integer.toHexString(number).toUpperCase();
    final int length = result.length();
    if (length == 4) {
      return result;
    } else if (result.length() < 4) {
      return ZEROS_4[length] + result;
    } else {
      return result.substring(length - 4);
    }
  }
}

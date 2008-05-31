package de.markusheiden.c64dt.util;

import org.springframework.util.Assert;

/**
 * Helper class for handling hexadecimal values.
 */
public class HexUtil {

  /**
   * Format max. 2 digit number hexadecimal.
   *
   * @param number number
   * @return
   */
  public static String format2(int number) {
    String result = "00" + Integer.toHexString(number).toUpperCase();
    if (result.length() > 2) {
      result = result.substring(result.length() - 2);
    }

    Assert.notNull(result, "Postcondition: result != null");
    return result;
  }

  /**
   * Format max. 4 digit number hexadecimal.
   *
   * @param number number
   * @return
   */
  public static String format4(int number) {
    String result = "0000" + Integer.toHexString(number).toUpperCase();
    if (result.length() > 4) {
      result = result.substring(result.length() - 4);
    }

    Assert.notNull(result, "Postcondition: result != null");
    return result;
  }
}

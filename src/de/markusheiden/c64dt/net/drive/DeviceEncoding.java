package de.markusheiden.c64dt.net.drive;

import de.markusheiden.c64dt.charset.C64Charset;

/**
 * Encoding for the net drive.
 */
public class DeviceEncoding {
  /**
   * Encodes a char to c64 encoding.
   *
   * @param decoded char
   */
  public static byte encode(char decoded) {
    return encode(String.valueOf(decoded))[0];
  }

  /**
   * Encodes a string to c64 encoding.
   *
   * @param decoded string
   */
  public static byte[] encode(String decoded) {
    return C64Charset.LOWER.toBytes(decoded.toUpperCase());
  }

  /**
   * Decodes a c64 encoded string.
   *
   * @param encoded c64 encoded string
   */
  public static String decode(byte[] encoded) {
    return C64Charset.LOWER.toString(encoded).toUpperCase();
  }
}

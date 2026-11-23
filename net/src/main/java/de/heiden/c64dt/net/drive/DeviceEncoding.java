package de.heiden.c64dt.net.drive;

import de.heiden.c64dt.charset.C64Charset;

/**
 * Encoding for the net drive.
 */
public class DeviceEncoding {
  /**
   * Encodes a char to C64 encoding.
   *
   * @param decoded Char.
   */
  public static byte encode(char decoded) {
    return encode(String.valueOf(decoded))[0];
  }

  /**
   * Encodes a string to C64 encoding.
   *
   * @param decoded String.
   */
  public static byte[] encode(String decoded) {
    return C64Charset.LOWER.toBytes(decoded.toUpperCase());
  }

  /**
   * Decodes a C64 encoded string.
   *
   * @param encoded C64 encoded string.
   */
  public static String decode(byte[] encoded) {
    return C64Charset.LOWER.toString(encoded).toUpperCase();
  }
}

package de.markusheiden.c64dt.util;

import org.springframework.util.Assert;

/**
 * Util class for handling bytes.
 */
public class ByteUtil {
  /**
   * Convert unsigned byte to int.
   */
  public static int toByte(int b) {
    return b & 0xFF;
  }

  /**
   * Read byte from byte array.
   */
  public static int toByte(byte[] data, int pos) {
    Assert.notNull(data, "Precondition: data != null");
    Assert.isTrue(pos < data.length, "Precondition: pos < data.length");

    return toByte(data[pos]);
  }

  /**
   * Read word from byte array.
   */
  public static int toWord(byte[] data, int pos) {
    Assert.notNull(data, "Precondition: data != null");
    Assert.isTrue(pos + 1 < data.length, "Precondition: pos + 1 < data.length");

    return toByte(data[pos]) + (toByte(data[pos + 1]) << 8);
  }

  /**
   * Highbyte of 16 bit word.
   *
   * @param word 16 bit word
   */
  public static int hi(int word) {
    Assert.isTrue(word >= 0x0000 && word <= 0xFFFF, "Precondition: word >= 0x0000 && word <= 0xFFFF");

    return (byte) (word >> 8);
  }

  /**
   * Lowbyte of 16 bit word.
   *
   * @param word 16 bit word
   */
  public static int lo(int word) {
    Assert.isTrue(word >= 0x0000 && word <= 0xFFFF, "Precondition: word >= 0x0000 && word <= 0xFFFF");

    return (byte) (word & 0xFF);
  }
}

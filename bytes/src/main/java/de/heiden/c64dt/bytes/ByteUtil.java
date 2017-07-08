package de.heiden.c64dt.bytes;

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
   * Convert unsigned word to int.
   */
  public static int toWord(int low, int high) {
    return toByte(low) + (toByte(high) << 8);
  }

  /**
   * Read word from byte array.
   */
  public static int toWord(byte[] data, int pos) {
    Assert.notNull(data, "Precondition: data != null");
    Assert.isTrue(pos + 1 < data.length, "Precondition: pos + 1 < data.length");

    return toWord(data[pos], data[pos + 1]);
  }

  /**
   * Highbyte of 16 bit word.
   *
   * @param word 16 bit word
   */
  public static int hi(int word) {
    Assert.isTrue(word >= 0x0000 && word <= 0xFFFF, "Precondition: word >= 0x0000 && word <= 0xFFFF");

    return (word >> 8) & 0xFF;
  }

  /**
   * Lowbyte of 16 bit word.
   *
   * @param word 16 bit word
   */
  public static int lo(int word) {
    Assert.isTrue(word >= 0x0000 && word <= 0xFFFF, "Precondition: word >= 0x0000 && word <= 0xFFFF");

    return word & 0xFF;
  }
}

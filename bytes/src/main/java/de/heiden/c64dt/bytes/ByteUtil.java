package de.heiden.c64dt.bytes;

import static org.bitbucket.cowwoc.requirements.core.Requirements.requireThat;

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
    requireThat("data", data).isNotNull();
    requireThat("pos", pos).isLessThanOrEqualTo("data.length", data.length);

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
    requireThat("data", data).isNotNull();
    requireThat("pos + 1", pos + 1).isLessThan("data.length", data.length);

    return toWord(data[pos], data[pos + 1]);
  }

  /**
   * Highbyte of 16 bit word.
   *
   * @param word 16 bit word
   */
  public static int hi(int word) {
    requireThat("word", word).isGreaterThanOrEqualTo(0x0000).isLessThanOrEqualTo(0xFFFF);

    return (word >> 8) & 0xFF;
  }

  /**
   * Lowbyte of 16 bit word.
   *
   * @param word 16 bit word
   */
  public static int lo(int word) {
    requireThat("word", word).isGreaterThanOrEqualTo(0x0000).isLessThanOrEqualTo(0xFFFF);

    return word & 0xFF;
  }
}

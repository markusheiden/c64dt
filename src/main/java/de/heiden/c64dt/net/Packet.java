package de.heiden.c64dt.net;

import java.util.Arrays;

/**
 * Packet.
 */
public class Packet {
  private final byte[] data;
  private int length;

  /**
   * Constructor for empty packet.
   *
   * @param max Maximum packet size
   */
  public Packet(int max) {
    this.data = new byte[max];
    this.length = 0;
  }

  /**
   * Constructor for packet with given data.
   *
   * @param data Data
   */
  public Packet(byte[] data) {
    this.data = data;
    this.length = data.length;
  }

  /**
   * Add data to packet.
   *
   * @param data data
   */
  public void addByte(int data) {
    this.data[length++] = (byte) data;
  }

  /**
   * Add data to packet.
   *
   * @param data data
   */
  public void addByte(int... data) {
    for (int d : data) {
      this.data[length++] = (byte) d;
    }
  }

  /**
   * Add data to packet.
   *
   * @param data data
   */
  public void addData(byte... data) {
    addData(data, 0, data.length);
  }

  /**
   * Add data to packet.
   *
   * @param data data
   */
  public void addData(byte[] data, int offset, int length) {
    System.arraycopy(data, offset, this.data, this.length, length);
    this.length += length;
  }

  /**
   * Add padding byte to packet if packet length is not even.
   */
  public void pad() {
    if (length % 2 != 0) {
      addByte(0x00);
    }
  }

  /**
   * Packet data.
   */
  public byte[] getData() {
    return Arrays.copyOf(data, length);
  }
}

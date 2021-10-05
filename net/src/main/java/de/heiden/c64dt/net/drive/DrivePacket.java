package de.heiden.c64dt.net.drive;

import java.io.IOException;

import de.heiden.c64dt.bytes.ByteUtil;

import static de.heiden.c64dt.common.Requirements.R;

/**
 * Packet for drive.
 */
public class DrivePacket {
  private static final int IDX_SERVICE = 2;
  private static final int IDX_SEQUENCE = 3;
  private static final int IDX_LOGICAL_FILE = 4;
  private static final int IDX_CHANNEL = 5;
  private static final int IDX_DEVICE = 6;
  private static final int IDX_SIZE = 7;
  private static final int IDX_DATA = 8;

  private final byte[] data;

  /**
   * Constructor.
   *
   * @param data Data
   */
  public DrivePacket(byte[] data) {
    this.data = data;
  }

  /**
   * Number of the requested service.
   */
  public byte getService() {
    return data[IDX_SERVICE];
  }

  /**
   * Number of the requested service.
   */
  public void setService(byte service) {
    data[IDX_SERVICE] = service;
  }

  /**
   * Sequence number.
   */
  public byte getSequence() {
    return data[IDX_SEQUENCE];
  }

  /**
   * Logical file number.
   */
  public int getLogicalFile() {
    return ByteUtil.toByte(data[IDX_LOGICAL_FILE]);
  }

  /**
   * Channel number.
   * 0-15
   */
  public int getChannel() {
    return ByteUtil.toByte(data[IDX_CHANNEL] & 0x0F);
  }

  /**
   * Device number.
   */
  public int getDevice() {
    return ByteUtil.toByte(data[IDX_DEVICE]);
  }

  /**
   * Get size of payload data.
   */
  public int getSize() {
    return ByteUtil.toByte(data[IDX_SIZE]);
  }

  /**
   * Get first byte of payload data.
   */
  public int getData0() throws IOException {
    if (getSize() != 1) {
      throw new IOException("Invalid packet getSize");
    }

    return ByteUtil.toByte(data[IDX_DATA]);
  }

  /**
   * Get payload data.
   */
  public byte[] getData() throws IOException {
    int size = getSize();
    if (size >= IDX_DATA + data.length) {
      throw new IOException("Invalid packet size");
    }

    byte[] result = new byte[size];
    System.arraycopy(data, IDX_DATA, result, 0, result.length);

    R.requireThat(result, "result").isNotNull();
    return result;
  }
}

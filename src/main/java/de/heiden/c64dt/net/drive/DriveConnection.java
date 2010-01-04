package de.heiden.c64dt.net.drive;

import de.heiden.c64dt.net.AbstractConnection;
import de.heiden.c64dt.util.ByteUtil;
import org.springframework.util.Assert;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * Connection of net drive server.
 */
public class DriveConnection extends AbstractConnection {
  public static final int DEFAULT_PORT = 6463;

  public static final byte SERVICE_OPEN = 0x01;
  public static final byte SERVICE_CHKIN = 0x02;
  public static final byte SERVICE_READ = 0x03;
  public static final byte SERVICE_CLOSE = 0x04;
  public static final byte SERVICE_WRITE = 0x05;
  public static final byte SERVICE_DATA = 0x20;

  private static final int IDX_SERVICE = 2;
  private static final int IDX_SEQUENCE = 3;
  private static final int IDX_LOGICAL_FILE = 4;
  private static final int IDX_CHANNEL = 5;
  private static final int IDX_DEVICE = 6;
  private static final int IDX_SIZE = 7;
  private static final int IDX_DATA = 8;

  private static final int MAX_PACKET = 0x90;

  private int received;

  /**
   * Constructor.
   */
  public DriveConnection() throws IOException {
    this(DEFAULT_PORT);
  }

  /**
   * Constructor.
   */
  public DriveConnection(int port) throws IOException {
    super(new InetSocketAddress(InetAddress.getLocalHost(), port), null, MAX_PACKET, 0xAD, 0xF8);
  }

  /**
   * Number of the requested service.
   */
  public byte getService() {
    return input[IDX_SERVICE];
  }

  /**
   * Number of the requested service.
   */
  public void setService(byte service) {
    input[IDX_SERVICE] = service;
  }

  /**
   * Is this a request a request to resend the last reply?.
   */
  public boolean isResendRequest() {
    return input[IDX_SEQUENCE] == sequence;
  }

  /**
   * Logical file number.
   */
  public int getLogicalFile() {
    return ByteUtil.toByte(input[IDX_LOGICAL_FILE]);
  }

  /**
   * Channel number.
   * 0-15
   */
  public int getChannel() {
    return ByteUtil.toByte(input[IDX_CHANNEL] & 0x0F);
  }

  /**
   * Device number.
   */
  public int getDevice() {
    return ByteUtil.toByte(input[IDX_DEVICE]);
  }

  /**
   * Get size of payload data.
   */
  public int getSize() {
    return ByteUtil.toByte(input[IDX_SIZE]);
  }

  /**
   * Get first byte of payload data.
   */
  public int getData0() throws IOException {
    if (getSize() != 1) {
      throw new IOException("Invalid packet getSize");
    }

    return ByteUtil.toByte(input[IDX_DATA]);
  }

  /**
   * Get payload data.
   */
  public byte[] getData() throws IOException {
    int size = getSize();
    if (size >= MAX_PACKET) {
      throw new IOException("Invalid packet getSize");
    }

    byte[] result = new byte[size];
    System.arraycopy(input, IDX_DATA, result, 0, result.length);

    Assert.notNull(result, "Postcondition: result != null");
    return result;
  }

  /**
   * Send reply with error code.
   *
   * @param error error code
   */
  public synchronized void sendReply(Error error) throws IOException {
    sequence = input[IDX_SEQUENCE];

    System.arraycopy(input, 0, output, 0, IDX_SIZE);
    output[IDX_SIZE] = 0x01;
    output[IDX_DATA] = error.getResult();
    output[9] = 0x00; // padding byte

    sendPacket(10);
  }

  /**
   * Send reply with payload data.
   *
   * @param data payload data
   */
  public void sendReply(byte[] data) throws IOException {
    Assert.notNull(data, "Precondition: data != null");

    byte size = (byte) data.length;
    if (IDX_DATA + size >= MAX_PACKET) {
      throw new IOException("Invalid packet getSize");
    }

    sequence = input[IDX_SEQUENCE];

    System.arraycopy(input, 0, output, 0, IDX_SIZE);
    output[IDX_SERVICE] = SERVICE_DATA;
    output[IDX_SIZE] = size;
    System.arraycopy(data, 0, output, IDX_DATA, data.length);
    if (data.length % 2 != 0) {
      output[IDX_DATA + data.length] = 0x00;
    }

    sendPacket(IDX_DATA + data.length);
  }
}

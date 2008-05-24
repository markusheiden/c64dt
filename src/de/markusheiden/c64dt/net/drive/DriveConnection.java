package de.markusheiden.c64dt.net.drive;

import de.markusheiden.c64dt.util.ByteUtil;
import de.markusheiden.c64dt.net.AbstractConnection;
import org.springframework.util.Assert;

import java.io.IOException;
import java.net.ServerSocket;

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

  private ServerSocket server;
  private int received;

  /**
   * Constructor.
   */
  public DriveConnection() {
    this(DEFAULT_PORT);
  }

  /**
   * Constructor.
   */
  public DriveConnection(int port) {
    super(DEFAULT_PORT, MAX_PACKET, 0xAD, 0xF8);
  }

  public synchronized void open() throws IOException {
    server = new ServerSocket(port);
    server.setReceiveBufferSize(MAX_PACKET);
    open(server.accept());
  }

  /**
   * Receive a request.
   */
  public synchronized void receiveRequest() throws IOException {
    received = is.read(packet);
    if (received <= 0) {
      throw new IOException("Connection lost");
    } else if (!isValid()) {
      throw new IOException("Invalid packet");
    }
  }

  /**
   * Number of the requested service.
   */
  public byte getService() {
    return packet[IDX_SERVICE];
  }

  /**
   * Number of the requested service.
   */
  public void setService(byte service) {
    packet[IDX_SERVICE] = service;
  }

  /**
   * Is this a request a request to resend the last reply?.
   */
  public boolean isResendRequest() {
    return packet[IDX_SEQUENCE] == sequence;
  }

  /**
   * Logical file number.
   */
  public byte getLogicalFile() {
    return packet[IDX_LOGICAL_FILE];
  }

  /**
   * Channel number.
   * 0-15
   */
  public byte getChannel() {
    return packet[IDX_CHANNEL];
  }

  /**
   * Device number.
   */
  public byte getDevice() {
    return packet[IDX_DEVICE];
  }

  /**
   * Get size of payload data.
   */
  public int getSize() {
    return ByteUtil.toByte(packet[IDX_SIZE]);
  }

  /**
   * Get first byte of payload data.
   */
  public byte getData0() throws IOException {
    if (getSize() != 1) {
      throw new IOException("Invalid packet size");
    }

    return packet[0];
  }

  /**
   * Get payload data.
   */
  public byte[] getData() throws IOException {
    int size = getSize();
    if (size >= MAX_PACKET) {
      throw new IOException("Invalid packet size");
    }

    byte[] result = new byte[size];
    System.arraycopy(packet, IDX_DATA, result, 0, result.length);

    Assert.notNull(result, "Postcondition: result != null");
    return result;
  }

  /**
   * Send reply with error code.
   *
   * @param error error code
   */
  public synchronized void sendReply(Error error) throws IOException {
    sequence = packet[IDX_SEQUENCE];

    packet[IDX_SIZE] = 0x01;
    packet[IDX_DATA] = error.getResult();
    packet[9] = 0x00; // padding byte
    os.write(packet, 0, 10);
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
      throw new IOException("Invalid packet size");
    }

    packet[IDX_SERVICE] = SERVICE_DATA;
    packet[IDX_SIZE] = size;
    System.arraycopy(data, 0, packet, IDX_DATA, data.length);
  }

  /**
   * Send the last reply again.
   */
  public synchronized void resendReply() throws IOException {
    os.write(packet, 0, 10);
  }

  public void close() throws IOException {
    try {
      super.close();
    } finally {
      server.close();
    }
  }
}

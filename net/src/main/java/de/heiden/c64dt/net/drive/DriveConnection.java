package de.heiden.c64dt.net.drive;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import de.heiden.c64dt.net.AbstractConnection;
import de.heiden.c64dt.net.Packet;

import static com.github.cowwoc.requirements10.java.DefaultJavaValidators.requireThat;

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

  private DrivePacket request = null;

  /**
   * Constructor using the default port.
   */
  public DriveConnection() throws IOException {
    this(DEFAULT_PORT);
  }

  /**
   * Constructor.
   *
   * @param port port to use
   */
  public DriveConnection(int port) throws IOException {
    super(new InetSocketAddress(InetAddress.getLocalHost(), port), null, MAX_PACKET, 0xAD, 0xF8);
  }

  /**
   * Wait for a request from the C64.
   * Handles resend request automatically.
   */
  public synchronized DrivePacket waitForRequest() throws IOException {
    request = new DrivePacket(receivePacket().getData());
    if (sequence == request.getSequence()) {
      // TODO 2011-01-09 mh: check, if at least one packet has been sent
      resendPacket();
    }

    return request;
  }

  /**
   * Send reply with error code.
   *
   * @param error error code
   */
  public synchronized void sendReply(Error error) throws IOException {
    sequence = request.getSequence();

    // TODO 2011-01-09 mh: check packet creation
    Packet packet = new Packet(MAX_PACKET);
    packet.addByte(request.getSize());
    packet.addByte(0x01);
    packet.addByte(error.getResult());

    sendPacket(packet);
  }

  /**
   * Send reply with payload data.
   *
   * @param data payload data
   */
  public void sendReply(byte[] data) throws IOException {
    requireThat(data, "data").isNotNull();

    byte size = (byte) data.length;
//    if (IDX_DATA + size >= MAX_PACKET) {
//      throw new IOException("Invalid packet size");
//    }

    sequence = request.getSequence();

    // TODO 2011-01-09 mh: check packet creation
    Packet packet = new Packet(MAX_PACKET);
    packet.addByte(request.getSize());
    packet.addByte(SERVICE_DATA);
    packet.addByte(size);
    packet.addData(data);

    sendPacket(packet);
  }
}

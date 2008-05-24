package de.markusheiden.c64dt.net.code;

import static de.markusheiden.c64dt.util.ByteUtil.hi;
import static de.markusheiden.c64dt.util.ByteUtil.lo;
import de.markusheiden.c64dt.net.AbstractConnection;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * IP connection to a c64.
 */
public class C64Connection extends AbstractConnection {
  public static final int DEFAULT_PORT = 6462;

  private static final int IDX_SEQUENCE = 2;
  private static final int IDX_REPLY = 3;

  private static final int MAX_PACKET = 0x90;

  private final InetAddress address;

  /**
   * Constructor.
   */
  public C64Connection(InetAddress address) {
    this(address, DEFAULT_PORT);
  }

  /**
   * Constructor.
   */
  public C64Connection(InetAddress address, int port) {
    super(port, MAX_PACKET, 0xCA, 0x1F);
    Assert.notNull(address, "Precondition: address != null");

    this.address = address;
  }

  public synchronized void open() throws IOException {
    open(new Socket(address, port));
  }

  /**
   * Send data to memory location.
   *
   * @param address address
   * @param data data
   */
  protected synchronized boolean sendData(int address, byte[] data) throws IOException {
    Assert.notNull(data, "Precondition: data != null");
    Assert.isTrue(4 + data.length <= getPacketSize(), "Precondition: 4 + data.length <= getPacketSize()");
    Assert.isTrue(isOpen(), "Precondition: isOpen()");

    sendPacket(4, hi(address), lo(address), hi(data.length), lo(data.length));
    os.write(data);
    // Align to 16 bit words
    if (data.length % 2 != 0) {
      os.write(0x00);
    }
    return receiveReply();
  }

  /**
   * Fill memory area.
   *
   * @param address address
   * @param length length of memory area
   * @param fill fill byte
   */
  protected synchronized boolean sendFill(int address, int length, byte fill) throws IOException {
    Assert.isTrue(isOpen(), "Precondition: isOpen()");

    sendPacket(5, hi(address), lo(address), hi(length), lo(length), fill, (byte) 0x00);
    return receiveReply();
  }

  /**
   * Set PC.
   *
   * @param address address
   */
  protected synchronized boolean sendJump(int address) throws IOException {
    Assert.isTrue(isOpen(), "Precondition: isOpen()");

    sendPacket(6, hi(address), lo(address));
    return receiveReply();
  }

  /**
   * Execute code at PC.
   */
  protected synchronized boolean sendExecute() throws IOException {
    Assert.isTrue(isOpen(), "Precondition: isOpen()");

    sendPacket(7);
    return receiveReply();
  }

  /**
   * Create packet.
   *
   * @param service Service
   * @param data data of the packet
   */
  protected synchronized void sendPacket(int service, byte... data) throws IOException {
    sequence++;
    writeMagic();
    os.write(sequence);
    os.write(service);
    os.write(data);
  }

  /**
   * Send packet and receive immediate reply.
   */
  protected synchronized boolean receiveReply() throws IOException {
    // really send
    os.flush();

    // receive
    try {
      // Just one reply-packet expected -> should be read at once
      int read = is.read(packet);
      byte reply = packet[IDX_REPLY];
      if (read != packet.length || !isValid() || packet[IDX_SEQUENCE] != sequence) {
        throw new IOException("Wrong reply");
      }

      return reply == 0;
    } catch (InterruptedIOException e) {
      throw new IOException("No reply");
    }
  }
}

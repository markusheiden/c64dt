package de.markusheiden.c64dt.net.code;

import static de.markusheiden.c64dt.util.ByteUtil.hi;
import static de.markusheiden.c64dt.util.ByteUtil.lo;
import de.markusheiden.c64dt.net.AbstractConnection;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * IP connection to a c64.
 */
public class C64Connection extends AbstractConnection {
  public static final int DEFAULT_PORT = 6462;

  private static final int IDX_SEQUENCE = 2;
  private static final int IDX_SERVICE = 3;
  private static final int IDX_DATA = 4;

  private static final int IDX_REPLY = 3; // TODO correct?

  private static final int MAX_PACKET = 0x90;

  private final InetAddress address;

  /**
   * Constructor.
   */
  public C64Connection(InetAddress address) throws IOException {
    this(DEFAULT_PORT, address, DEFAULT_PORT);
  }

  /**
   * Constructor.
   */
  public C64Connection(int sourcePort, InetAddress address, int destinationPort) throws IOException {
    super(new InetSocketAddress(InetAddress.getLocalHost(), sourcePort), new InetSocketAddress(address, destinationPort), MAX_PACKET, 0xCA, 0x1F);
    Assert.notNull(address, "Precondition: address != null");

    this.address = address;
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
    System.arraycopy(data, 0, output, IDX_DATA, data.length);
    // Align to 16 bit words
    if (data.length % 2 != 0) {
      output[IDX_DATA + data.length] = 0x00;
    }
    sendPacket(IDX_DATA + data.length); // TODO pad!!!

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
    output[IDX_SEQUENCE] = sequence;
    output[IDX_SERVICE] = (byte) service;
    System.arraycopy(data, 0, output, IDX_DATA, data.length);

    sendPacket(IDX_DATA + data.length);
  }

  /**
   * Send packet and receive immediate reply.
   */
  protected synchronized boolean receiveReply() throws IOException {
    receivePacket();
    byte reply = input[IDX_REPLY];
    // TODO check packet.length?
    if (!isValid() || input[IDX_SEQUENCE] != sequence) {
      throw new IOException("Wrong reply");
    }

    return reply == 0;
  }
}

package de.heiden.c64dt.net.code;

import de.heiden.c64dt.net.AbstractConnection;
import de.heiden.c64dt.net.Packet;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;

import static de.heiden.c64dt.util.AddressUtil.assertValidAddress;
import static de.heiden.c64dt.util.ByteUtil.hi;
import static de.heiden.c64dt.util.ByteUtil.lo;

/**
 * IP connection to a c64.
 */
public class C64Connection extends AbstractConnection {
  public static final int DEFAULT_PORT = 6462;

  private static final int IDX_SEQUENCE = 2;
  private static final int IDX_SERVICE = 3;
  private static final int IDX_REPLY = 3;
  private static final int IDX_DATA = 4;

  private static final int MAGIC1 = 0xCA;
  private static final int MAGIC2 = 0x1F;

  /**
   * Constructor using default ports.
   *
   * @param address Remote address of C64
   */
  public C64Connection(InetAddress address) throws IOException {
    this(DEFAULT_PORT, address, DEFAULT_PORT);
  }

  /**
   * Constructor.
   *
   * @param sourcePort Port on this machine
   * @param address Remote address of C64
   * @param destinationPort Port of C64
   */
  public C64Connection(int sourcePort, InetAddress address, int destinationPort) throws IOException {
    super(new InetSocketAddress(InetAddress.getLocalHost(), sourcePort), new InetSocketAddress(address, destinationPort), MAX_PACKET, MAGIC1, MAGIC2);
    Assert.notNull(address, "Precondition: address != null");
  }

  /**
   * Ping C64.
   */
  public synchronized boolean ping() {
    try
    {
      Packet packet = createPacket(0);
      sendPacket(packet);
      return isAck(receivePacket());
    }
    catch (IOException e)
    {
      return false;
    }
  }

  /**
   * Send data to memory location.
   *
   * @param address address
   * @param data data
   */
  public synchronized void write(int address, byte... data) throws IOException {
    assertValidAddress(address);
    Assert.notNull(data, "Precondition: data != null");
    Assert.isTrue(4 + data.length <= getPacketSize(), "Precondition: 4 + data.length <= getPacketSize()");
    Assert.isTrue(isOpen(), "Precondition: isOpen()");

    for (int ptr = 0, remain = data.length; remain > 0; ptr += 128, remain -= 128) {
      int length = remain > 128? 128 : remain;
      Packet packet = createPacket(4, hi(address), lo(address), hi(length), lo(length));
      packet.addData(data, ptr, length);
      sendPacketGetReply(packet);
    }
  }

  /**
   * Fill memory area.
   *
   * @param address address
   * @param length length of memory area
   * @param fill fill byte
   */
  public synchronized void fill(int address, int length, int fill) throws IOException {
    assertValidAddress(address);
    Assert.isTrue(isOpen(), "Precondition: isOpen()");

    Packet packet = createPacket(5, hi(address), lo(address), hi(length), lo(length), fill, (byte) 0x00);
    sendPacketGetReply(packet);
  }

  /**
   * Set PC.
   *
   * @param address address
   */
  public synchronized void jump(int address) throws IOException {
    assertValidAddress(address);
    Assert.isTrue(isOpen(), "Precondition: isOpen()");

    Packet packet = createPacket(6, hi(address), lo(address));
    sendPacketGetReply(packet);
  }

  /**
   * Execute basic program by "RUN".
   */
  public synchronized void run() throws IOException {
    Assert.isTrue(isOpen(), "Precondition: isOpen()");

    Packet packet = createPacket(7);
    sendPacketGetReply(packet);
  }

  /**
   * Read data from memory location ???.
   * Command defined but not implemented in in CodeNet client.
   * This method is currently broken!
   * TODO 2011-01-09 mh: Fix this
   *
   * @param address address
   * @param length number of bytes to read
   * @return read data
   */
  public synchronized byte[] read(int address, int length) throws IOException {
    assertValidAddress(address);
    Assert.isTrue(isOpen(), "Precondition: isOpen()");

    Packet packet = createPacket(8, hi(address), lo(address), hi(length), lo(length));
    Packet answer = sendPacketGetReply(packet);
    byte[] result = new byte[length];
    // TODO 2011-01-09 mh: C64 currently just gets returns an ack
    System.arraycopy(answer.getData(), IDX_DATA, result, 0, length);

    return result;
  }

  /**
   * Create packet.
   *
   * @param service Service
   * @param data data of the packet
   */
  protected synchronized Packet createPacket(int service, int... data) throws IOException {
    sequence++;
    Packet result = new Packet(MAX_PACKET);
    result.addByte(MAGIC1);
    result.addByte(MAGIC2);
    result.addByte(sequence);
    result.addByte(service);
    result.addByte(data);

    return result;
  }

  /**
   * Send packet and receive reply with retries.
   *
   * @param packet packet
   * @return received packet (e.g. ack)
   */
  protected synchronized Packet sendPacketGetReply(Packet packet) throws IOException
  {
    Packet ack = null;
    try
    {
      for (int i = 0; i < 3; i++)
      {
        sendPacket(packet);
        ack = receivePacket();
        if (isAck(ack)) {
          return ack;
        }
      }
    }
    catch (SocketTimeoutException e)
    {
      throw new IOException("C64 does not answer");
    }

    if (ack == null) {
      throw new IOException("C64 not reachable");
    }

    throw new IOException("Command failed with error " + ack.getData()[IDX_REPLY]);
  }

  /**
   * Check packet for a valid acknowledge.
   *
   * @param ack packet
   */
  protected boolean isAck(Packet ack) throws IOException
  {
    byte[] data = ack.getData();

    if (!isValid(ack))
    {
      throw new IOException("Invalid packet");
    }
    if (data[IDX_SEQUENCE] != sequence)
    {
      throw new IOException("Wrong sequence number");
    }
    if (data[IDX_SERVICE] != 1)
    {
      throw new IOException("Invalid ACK");
    }

    return data[IDX_REPLY] < 2;
  }
}

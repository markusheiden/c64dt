package de.heiden.c64dt.net.code;

import de.heiden.c64dt.net.AbstractConnection;
import de.heiden.c64dt.net.Packet;
import org.springframework.util.Assert;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

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
  private static final int IDX_DATA = 4;

  private static final int IDX_REPLY = 3; // TODO correct?

  private static final int MAGIC1 = 0xCA;
  private static final int MAGIC2 = 0x1F;
  private static final int MAX_PACKET = 0x90;

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
   * Send data to memory location.
   *
   * @param address address
   * @param data data
   */
  public synchronized boolean data(int address, byte... data) throws IOException {
    assertValidAddress(address);
    Assert.notNull(data, "Precondition: data != null");
    Assert.isTrue(4 + data.length <= getPacketSize(), "Precondition: 4 + data.length <= getPacketSize()");
    Assert.isTrue(isOpen(), "Precondition: isOpen()");

    Packet packet = createPacket(4, hi(address), lo(address), hi(data.length), lo(data.length));
    packet.add(data);
    return sendReceivePacket(packet);
  }

  /**
   * Fill memory area.
   *
   * @param address address
   * @param length length of memory area
   * @param fill fill byte
   */
  public synchronized boolean fill(int address, int length, byte fill) throws IOException {
    assertValidAddress(address);
    Assert.isTrue(isOpen(), "Precondition: isOpen()");

    Packet packet = createPacket(5, hi(address), lo(address), hi(length), lo(length), fill, (byte) 0x00);
    return sendReceivePacket(packet);
  }

  /**
   * Set PC.
   *
   * @param address address
   */
  public synchronized boolean jump(int address) throws IOException {
    assertValidAddress(address);
    Assert.isTrue(isOpen(), "Precondition: isOpen()");

    Packet packet = createPacket(6, hi(address), lo(address));
    return sendReceivePacket(packet);
  }

  /**
   * Execute code at PC.
   */
  public synchronized boolean execute() throws IOException {
    Assert.isTrue(isOpen(), "Precondition: isOpen()");

    Packet packet = createPacket(7);
    return sendReceivePacket(packet);
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
    result.add(MAGIC1);
    result.add(MAGIC2);
    result.add(sequence);
    result.add(service);
    result.add(data);

    return result;
  }

  /**
   * Receive packet reply with retries.
   */
  protected synchronized boolean sendReceivePacket(Packet packet) throws IOException
  {
    for (int i = 0; i < 3; i++)
    {
      sendPacket(packet);
      if (receiveReplyPacket()) {
        return true;
      }
    }

    return false;
  }

  /**
   * Receive packet reply.
   */
  protected synchronized boolean receiveReplyPacket() throws IOException
  {
    receivePacket();
    byte reply = input[IDX_REPLY];
    if (!isValid() || input[IDX_SEQUENCE] != sequence) {
      throw new IOException("Wrong reply");
    }

    return reply <= 2;
  }

  /**
   * Manual test.
   */
  public static void main(String[] args) throws Exception
  {
    C64Connection connection = new C64Connection(InetAddress.getByName("192.168.2.64"));
    connection.open();
    connection.fill(0x0400, 999, (byte) 0x31);
    connection.close();
  }
}

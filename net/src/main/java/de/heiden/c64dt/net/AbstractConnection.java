package de.heiden.c64dt.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.Arrays;

/**
 * Common code for connections.
 */
public abstract class AbstractConnection {
  public static final int MAX_PACKET = 150;

  private static final int IDX_MAGIC1 = 0;
  private static final int IDX_MAGIC2 = 1;

  private final SocketAddress source;
  private SocketAddress destination;
  private SocketAddress lastDestination;
  protected final int packetSize;
  private final byte magic1;
  private final byte magic2;

  private DatagramSocket socket;
  private final byte[] input;
  private DatagramPacket lastInput;
  private DatagramPacket lastOutput;
  protected byte sequence;

  /**
   * Constructor.
   */
  protected AbstractConnection(SocketAddress source, SocketAddress destination, int packetSize, int magic1, int magic2) {
    this.source = source;
    this.destination = destination;
    this.packetSize = packetSize;
    this.magic1 = (byte) magic1;
    this.magic2 = (byte) magic2;

    socket = null;
    input = new byte[packetSize];
    lastInput = null;
    lastOutput = null;
    sequence = 0;
  }

  /**
   * Source address.
   */
  public String getSource() {
    return source.toString();
  }

  /**
   * Remote address.
   */
  public String getDestination() {
    SocketAddress result = destination != null ? destination : lastDestination;
    return result != null ? result.toString() : "unknown";
  }

  /**
   * Maximum packet size.
   */
  public int getPacketSize() {
    return packetSize;
  }

  /**
   * Is connection open?
   */
  public boolean isOpen() {
    return socket != null;
  }

  /**
   * Open connection.
   *
   * @throws IOException
   */
  public synchronized void open() throws IOException {
    socket = new DatagramSocket(source);
    socket.setSendBufferSize(256);
    socket.setReceiveBufferSize(256);
    socket.setSoTimeout(1000);
  }

  /**
   * Send a packet.
   */
  public synchronized void sendPacket(Packet packet) throws IOException {
    packet.pad();
    byte[] data = packet.getData();
    lastOutput = new DatagramPacket(data, data.length, destination != null ? destination : lastDestination);
    socket.send(lastOutput);
  }

  /**
   * Receive a packet.
   */
  public synchronized Packet receivePacket() throws IOException {
    lastInput = new DatagramPacket(input, input.length);
    socket.receive(lastInput);
    lastDestination = lastInput.getSocketAddress();

    Packet result = new Packet(Arrays.copyOf(lastInput.getData(), lastInput.getLength()));
    if (!isValid(result)) {
      throw new IOException("Invalid packet");
    }

    return result;
  }

  /**
   * Send the last packet again.
   */
  public synchronized void resendPacket() throws IOException {
    socket.send(lastOutput);
  }

  /**
   * Check magic bytes.
   */
  protected boolean isValid(Packet packet) {
    byte[] data = packet.getData();
    return data[IDX_MAGIC1] == magic1 && data[IDX_MAGIC2] == magic2;
  }

  /**
   * Close connection.
   */
  public void close() throws IOException {
    try {
      if (socket != null) {
        socket.close();
      }
    } finally {
      socket = null;
    }
  }

  /**
   * {@inheritDoc}
   * <p>
   * Ensure that socket gets closed.
   */
  @Override
  protected void finalize() throws Throwable {
    try {
      close();
    } finally {
      super.finalize();
    }
  }
}

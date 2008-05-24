package de.markusheiden.c64dt.net;

import org.springframework.util.Assert;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.SocketAddress;

/**
 * Common code for connections.
 */
public abstract class AbstractConnection {
  private static final int IDX_MAGIC1 = 0;
  private static final int IDX_MAGIC2 = 1;

  private final SocketAddress source;
  private SocketAddress destination;
  private SocketAddress lastDestination;
  protected final int packetSize;
  private final byte magic1;
  private final byte magic2;

  private DatagramSocket socket;
  protected final byte[] input;
  private DatagramPacket lastInput;
  protected final byte[] output;
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
    output = new byte[packetSize];
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
    SocketAddress result = destination != null? destination : lastDestination;
    return result != null? result.toString() : "unknown";
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
   * @throws java.io.IOException
   */
  public synchronized void open() throws IOException {
    socket = new DatagramSocket(source);
    socket.setSendBufferSize(256);
    socket.setReceiveBufferSize(256);
  }

  /**
   * Receive a packet.
   */
  public synchronized void receivePacket() throws IOException {
    lastInput = new DatagramPacket(input, input.length);
    socket.receive(lastInput);
    lastDestination = lastInput.getSocketAddress();
    if (!isValid()) {
      throw new IOException("Invalid packet");
    }
  }

  /**
   * Send a packet.
   */
  public synchronized void sendPacket(int length) throws IOException {
    lastOutput = new DatagramPacket(output, length, destination != null? destination : lastDestination);
    socket.send(lastOutput);
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
  protected boolean isValid() {
    return input[IDX_MAGIC1] == magic1 && input[IDX_MAGIC2] == magic2;
  }

  /**
   * Write magic bytes.
   */
  protected void writeMagic() throws IOException {
    output[IDX_MAGIC1] = magic1;
    output[IDX_MAGIC2] = magic2;
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
   *
   * Ensure that socket gets closed.
   */
  protected void finalize() throws Throwable {
    try {
      close();
    } finally {
      super.finalize();
    }
  }
}

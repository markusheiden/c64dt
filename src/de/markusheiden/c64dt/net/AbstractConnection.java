package de.markusheiden.c64dt.net;

import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Common code for connections.
 */
public abstract class AbstractConnection {
  private static final int IDX_MAGIC1 = 0;
  private static final int IDX_MAGIC2 = 1;

  protected final int port;
  private final byte magic1;
  private final byte magic2;

  private Socket socket;
  protected OutputStream os;
  protected InputStream is;
  protected byte sequence;
  protected final byte[] packet;

  /**
   * Constructor.
   *
   * @param port port
   */
  protected AbstractConnection(int port, int packetSize, int magic1, int magic2) {
    this.port = port;
    this.packet = new byte[packetSize];
    this.magic1 = (byte) magic1;
    this.magic2 = (byte) magic2;

    socket = null;
    os = null;
    is = null;
    sequence = 0;
  }

  /**
   * Port.
   */
  public int getPort() {
    return port;
  }

  /**
   * Maximum packet size.
   */
  public int getPacketSize() {
    return packet.length;
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
  public abstract void open() throws IOException;

  /**
   * Open connection for a socket.
   *
   * @throws java.io.IOException
   */
  protected synchronized void open(Socket socket) throws IOException {
    Assert.isTrue(!isOpen(), "Precondition: !isOpen()");

    this.socket = socket;
    socket.setSendBufferSize(256);
    socket.setReceiveBufferSize(256);
    socket.setSoTimeout(2000);
    os = socket.getOutputStream();
    is = socket.getInputStream();
  }

  /**
   * Write magic bytes.
   */
  protected void writeMagic() throws IOException {
    os.write(magic1);
    os.write(magic2);
  }

  /**
   * Check magic bytes.
   */
  protected boolean isValid() {
    return packet[IDX_MAGIC1] == magic1 && packet[IDX_MAGIC2] == magic2;
  }

  /**
   * Close connection.
   */
  public void close() throws IOException {
    os = null;
    is = null;
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

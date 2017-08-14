package de.heiden.c64dt.net.drive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;

import static org.bitbucket.cowwoc.requirements.core.Requirements.requireThat;

/**
 * Net drive server.
 * Contains glue logic to connect drive connection with a device.
 */
public class NetDrive {
  /**
   * Logger.
   */
  private final Logger logger = LoggerFactory.getLogger(getClass());

  private volatile boolean isRunning;
  private Thread thread;
  private DriveConnection connection;
  private Device device;

  /**
   * Constructor.
   *
   * @param root root directory
   */
  public NetDrive(File root) throws IOException {
    this(root, DriveConnection.DEFAULT_PORT);
  }

  /**
   * Constructor.
   *
   * @param root root directory
   * @param port server port
   */
  public NetDrive(File root, int port) throws IOException {
    requireThat("root", root).isNotNull();
    requireThat("root.isDirectory()", root.isDirectory()).isTrue();
    requireThat("port", port).isBetween(0, 65536);

    isRunning = false;
    thread = null;
    connection = new DriveConnection(port);
    device = new Device(root);
  }

  /**
   * Is the server running?
   */
  public boolean isRunning() {
    return isRunning;
  }

  /**
   * Start server.
   */
  public void start() throws IOException {
    requireThat("isRunning()", isRunning()).isFalse();

    isRunning = true;
    thread = new Thread(new Server(), "Net drive server on " + connection.getSource());
    thread.start();
  }

  /**
   * Stop server.
   */
  public void stop() {
    requireThat("isRunning()", isRunning()).isTrue();

    isRunning = false;
    thread.interrupt();
    try {
      connection.close();
    } catch (IOException e) {
      logger.error("Failed to close drive connection");
      return;
    }
    try {
      thread.join();
    } catch (InterruptedException e) {
      // ignore
    }
  }

  private class Server implements Runnable {
    @Override
    public void run() {
      while (isRunning) {
        logger.info("Net drive server up and running");
        try {
          connection.open();
          while (isRunning) {
            try {
              DrivePacket received = connection.waitForRequest();
              logger.debug("Received packet from {}", connection.getDestination());
              switch (received.getService()) {
                case DriveConnection.SERVICE_OPEN: {
                  logger.info("OPEN {},{},{}", received.getLogicalFile(), received.getDevice(), received.getChannel());
                  try {
                    device.open(received.getChannel(), strip0(received.getData()));
                    connection.sendReply(Error.OK);
                  } catch (DeviceException e) {
                    connection.sendReply(e.getError());
                  }
                  break;
                }
                case DriveConnection.SERVICE_CHKIN: {
                  logger.info("CHKIN {},{},{}", received.getLogicalFile(), received.getDevice(), received.getChannel());
                  try {
                    device.incrementPosition(received.getChannel(), received.getData0());
                    connection.sendReply(Error.OK);
                  } catch (DeviceException e) {
                    connection.sendReply(e.getError());
                  }
                  break;
                }
                case DriveConnection.SERVICE_READ: {
                  logger.info("READ {},{},{}", received.getLogicalFile(), received.getDevice(), received.getChannel());
                  try {
                    byte[] data = device.read(received.getChannel(), received.getData0());
                    connection.sendReply(data);
                  } catch (DeviceException e) {
                    // TODO correct?
                    connection.sendReply(e.getError());
                  }
                  break;
                }
                case DriveConnection.SERVICE_WRITE: {
                  logger.info("WRITE {},{},{}", received.getLogicalFile(), received.getDevice(), received.getChannel());
                  try {
                    device.write(received.getChannel(), received.getData());
                    connection.sendReply(Error.OK);
                  } catch (DeviceException e) {
                    connection.sendReply(e.getError());
                  }
                  break;
                }
                case DriveConnection.SERVICE_CLOSE: {
                  logger.info("CLOSE {},{},{}", received.getLogicalFile(), received.getDevice(), received.getChannel());
                  try {
                    device.close(received.getChannel());
                    connection.sendReply(Error.OK);
                  } catch (DeviceException e) {
                    connection.sendReply(e.getError());
                  }
                  break;
                }
                default: {
                  logger.error("Unknown service {}", Integer.toHexString(received.getService()));
                }
              }
            } catch (IOException e) {
              logger.error("Server error", e);
            }
          }
        } catch (SocketException e) {
          logger.info("Aborted waiting for connections");
          return;
        } catch (IOException e) {
          logger.error("Failed to connect to server port", e);
          return;
        }
      }

      logger.info("Net drive server has been shut down");
    }
  }

  private byte[] strip0(byte[] data) {
    if (data[data.length - 1] != 0x00) {
      return data;
    }

    byte[] result = new byte[data.length - 1];
    System.arraycopy(data, 0, result, 0, result.length);

    return result;
  }
}

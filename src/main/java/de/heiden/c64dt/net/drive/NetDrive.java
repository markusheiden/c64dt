package de.heiden.c64dt.net.drive;

import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;

/**
 * Net drive server.
 * Contains glue logic to connect drive connection with a device.
 */
public class NetDrive {
  private final Logger logger = Logger.getLogger(getClass());

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
    Assert.notNull(root, "Precondition: root != null");
    Assert.isTrue(root.isDirectory(), "Precondition: root.isDirectory()");

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
    Assert.isTrue(!isRunning(), "Precondition: !isRunning()");

    isRunning = true;
    thread = new Thread(new Server(), "Net drive server on " + connection.getSource());
    thread.start();
  }

  /**
   * Stop server.
   */
  public void stop() {
    Assert.isTrue(isRunning(), "Precondition: isRunning()");

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
    public void run() {
      while(isRunning) {
        logger.info("Net drive server up and running");
        try {
          connection.open();
          while (isRunning) {
            try {
              connection.receivePacket();
              logger.debug("Received packet from " + connection.getDestination());
              if (connection.isResendRequest()) {
                connection.resendPacket();
                logger.info("Resend last reply");
              } else {
                switch (connection.getService()) {
                  case DriveConnection.SERVICE_OPEN: {
                    logger.info("OPEN " + connection.getLogicalFile() + "," + connection.getDevice() + "," + connection.getChannel());
                    try {
                      device.open(connection.getChannel(), strip0(connection.getData()));
                      connection.sendReply(Error.OK);
                    } catch (DeviceException e) {
                      connection.sendReply(e.getError());
                    }
                    break;
                  }
                  case DriveConnection.SERVICE_CHKIN: {
                    logger.info("CHKIN " + connection.getLogicalFile() + "," + connection.getDevice() + "," + connection.getChannel());
                    try {
                      device.incrementPosition(connection.getChannel(), connection.getData0());
                      connection.sendReply(Error.OK);
                    } catch (DeviceException e) {
                      connection.sendReply(e.getError());
                    }
                    break;
                  }
                  case DriveConnection.SERVICE_READ: {
                    logger.info("READ " + connection.getLogicalFile() + "," + connection.getDevice() + "," + connection.getChannel());
                    try {
                      byte[] data = device.read(connection.getChannel(), connection.getData0());
                      connection.sendReply(data);
                    } catch (DeviceException e) {
                      // TODO correct?
                      connection.sendReply(e.getError());
                    }
                    break;
                  }
                  case DriveConnection.SERVICE_WRITE: {
                    logger.info("WRITE " + connection.getLogicalFile() + "," + connection.getDevice() + "," + connection.getChannel());
                    try {
                      device.write(connection.getChannel(), connection.getData());
                      connection.sendReply(Error.OK);
                    } catch (DeviceException e) {
                      connection.sendReply(e.getError());
                    }
                    break;
                  }
                  case DriveConnection.SERVICE_CLOSE: {
                    logger.info("CLOSE " + connection.getLogicalFile() + "," + connection.getDevice() + "," + connection.getChannel());
                    try {
                      device.close(connection.getChannel());
                      connection.sendReply(Error.OK);
                    } catch (DeviceException e) {
                      connection.sendReply(e.getError());
                    }
                    break;
                  }
                  default: {
                    logger.error("Unknown service " + Integer.toHexString(connection.getService()));
                  }
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

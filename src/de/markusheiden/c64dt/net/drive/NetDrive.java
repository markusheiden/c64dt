package de.markusheiden.c64dt.net.drive;

import org.apache.log4j.Logger;
import org.springframework.util.Assert;

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
   */
  public NetDrive() {
    this(DriveConnection.DEFAULT_PORT);
  }

  /**
   * Constructor.
   */
  public NetDrive(int port) {
    isRunning = false;
    thread = null;
    connection = new DriveConnection(port);
    device = new Device();
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
    thread = new Thread(new Server(), "Net drive server on port " + connection.getPort());
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
      logger.info("Net drive server at port " + connection.getPort() + " up and running");
      try {
        connection.open();
      } catch (SocketException e) {
        logger.info("Aborted waiting for connections");
        return;
      } catch (IOException e) {
        logger.error("Failed to connect to server port", e);
        return;
      }

      while (isRunning) {
        try {
          connection.receiveRequest();
          if (connection.isResendRequest()) {
            connection.resendReply();
            logger.info("Resend last reply");
          } else {
            switch (connection.getService()) {
              case DriveConnection.SERVICE_OPEN: {
                logger.info("OPEN " + connection.getLogicalFile() + "," + connection.getDevice() + "," + connection.getChannel());
                try {
                  device.open(connection.getChannel(), connection.getData());
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

      logger.info("Net drive server at port " + connection.getPort() + " has been shut down");
    }
  }
}

package de.markusheiden.c64dt.net.drive;

import org.springframework.util.Assert;
import de.markusheiden.c64dt.charset.C64Charset;
import de.markusheiden.c64dt.net.drive.stream.AbstractStream;
import de.markusheiden.c64dt.net.drive.stream.NullStream;
import de.markusheiden.c64dt.net.drive.stream.IStream;
import de.markusheiden.c64dt.net.drive.stream.FileStream;
import de.markusheiden.c64dt.net.drive.path.IPath;
import de.markusheiden.c64dt.net.drive.path.Path;

import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * Simulated device.
 */
public class Device {
  private static final byte COMMAND_CHANNEL = 15;

  private IStream[] streams;
  private byte[] error;
  private IPath path;

  /**
   * Constructor.
   */
  public Device(File root) throws FileNotFoundException {
    Assert.notNull(root, "Precondition: root != null");
    Assert.isTrue(root.isDirectory(), "Precondition: root.isDirectory()");

    streams = new IStream[16];
    for (int i = 0; i < streams.length; i++) {
      streams[i] = new NullStream();
    }

    streams[COMMAND_CHANNEL] = new CommandStream();

    error = Error.OK.toBytes(0, 0);

    path = new Path(null, root);
  }

  public boolean isOpen(int channel) {
    assertValidChannel(channel);

    return streams[channel].isOpen();
  }

  public void open(int channel, byte[] file) throws DeviceException {
    assertValidChannel(channel);
    Assert.notNull(file, "Precondition: file != null");

    // create new channel from path
    if (channel != COMMAND_CHANNEL) {
      if (streams[channel].isOpen()) {
        close(channel);
      }

      try {
        streams[channel] = path.getFile(file);
      } catch (FileNotFoundException e) {
        error = Error.NOTFOUND.toBytes(0, 0);
        throw new DeviceException(Error.NOTFOUND);
      }
    }

    streams[channel].open(file);
  }

  public void incrementPosition(int channel, int increment) throws DeviceException {
    assertOpen(channel);

    streams[channel].incrementPosition(increment);
  }

  public byte[] read(int channel, int length) throws DeviceException {
    assertOpen(channel);

    try {
      return streams[channel].read(length);
    } catch (IOException e) {
      throw new DeviceException(Error.NOTFOUND);
    }
  }

  public void write(int channel, byte[] data) throws DeviceException {
    assertOpen(channel);

    try {
      streams[channel].write(data);
    } catch (IOException e) {
      throw new DeviceException(Error.NOTFOUND);
    }
  }

  public void close(int channel) throws DeviceException {
    assertValidChannel(channel);

    streams[channel].close();
  }

  protected final void assertValidChannel(int channel) {
    Assert.isTrue(channel >= 0 && channel <= 15, "Precondition: channel >= 0 && channel <= 15");
  }

  protected final void assertOpen(int channel) throws DeviceException {
    if (!isOpen(channel)) {
      throw new DeviceException(Error.FILENOTOPEN);
    }
  }

  /**
   * Stream for handling commands.
   */
  private class CommandStream extends AbstractStream {
    public boolean isOpen() {
      // the command channel is always open
      return true;
    }

    public void open(byte[] data) {
      String command = C64Charset.LOWER.toString(data);
      if (command.toLowerCase().startsWith("cd:")) {
        String path = command.substring(3);
        // TODO change path
      }
    }

    public byte[] doRead(int length) {
      // reset error to OK when last error has been completely read
      if (getPosition() >= error.length) {
        error = Error.OK.toBytes(0, 0);
        return new byte[0];
      }

      // read current error
      length = Math.min(length, error.length - getPosition());
      byte[] result = new byte[length];
      System.arraycopy(error, getPosition(), result, 0, length);

      Assert.notNull(result, "Postcondition: result != null");
      return result;
    }

    public void doWrite(byte[] data) {
      // nothing to do
    }
  }
}

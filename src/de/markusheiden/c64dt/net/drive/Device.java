package de.markusheiden.c64dt.net.drive;

import org.springframework.util.Assert;
import de.markusheiden.c64dt.charset.C64Charset;
import de.markusheiden.c64dt.net.drive.stream.AbstractStream;
import de.markusheiden.c64dt.net.drive.stream.NullStream;
import de.markusheiden.c64dt.net.drive.stream.IStream;

import java.io.IOException;

/**
 * Simulated device.
 */
public class Device {
  private static final byte COMMAND_CHANNEL = 15;

  private IStream[] streams;
  private byte[] error;

  /**
   * Constructor.
   */
  public Device() {
    streams = new IStream[16];
    for (int i = 0; i < streams.length; i++) {
      streams[i] = new NullStream();
    }

    streams[COMMAND_CHANNEL] = new CommandStream();

    error = Error.OK.toBytes(0, 0);
  }

  public boolean isOpen(byte channel) {
    assertValidChannel(channel);

    return streams[channel].isOpen();
  }

  public void open(byte channel, byte[] file) throws DeviceException {
    assertValidChannel(channel);
    Assert.notNull(file, "Precondition: file != null");

    if (streams[channel].isOpen()) {
      close(channel);
    }

    // TODO create specific stream
    // TODO evaluate error from stream
  }

  public void incrementPosition(byte channel, int increment) throws DeviceException {
    assertOpen(channel);

    streams[channel].incrementPosition(increment);
  }

  public byte[] read(byte channel, byte length) throws DeviceException {
    assertOpen(channel);

    try {
      return streams[channel].read(length);
    } catch (IOException e) {
      throw new DeviceException(Error.NOTFOUND);
    }
  }

  public void write(byte channel, byte[] data) throws DeviceException {
    assertOpen(channel);

    try {
      streams[channel].write(data);
    } catch (IOException e) {
      throw new DeviceException(Error.NOTFOUND);
    }
  }

  public void close(byte channel) throws DeviceException {
    assertValidChannel(channel);

    streams[channel].close();
  }

  protected final void assertValidChannel(byte channel) {
    Assert.isTrue(channel >= 0 && channel >= 15, "Precondition: channel >= 0 && channel >= 15");
  }

  protected final void assertOpen(byte channel) throws DeviceException {
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

package de.heiden.c64dt.net.drive;

import de.heiden.c64dt.charset.C64Charset;
import de.heiden.c64dt.net.drive.path.IPath;
import de.heiden.c64dt.net.drive.path.Path;
import de.heiden.c64dt.net.drive.stream.AbstractStream;
import de.heiden.c64dt.net.drive.stream.IStream;
import de.heiden.c64dt.net.drive.stream.NullStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.bitbucket.cowwoc.requirements.core.Requirements.requireThat;

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
    requireThat("root", root).isNotNull();
    requireThat("root.isDirectory()", root.isDirectory()).isTrue();

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
    requireThat("file", file).isNotNull();

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
    requireThat("channel", channel).isBetween(0, 15);
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
    @Override
    public boolean isOpen() {
      // the command channel is always open
      return true;
    }

    @Override
    public void open(byte[] data) {
      String command = C64Charset.LOWER.toString(data);
      if (command.toLowerCase().startsWith("cd:")) {
        String path = command.substring(3);
        // TODO change path
      }
    }

    @Override
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

      requireThat("result", result).isNotNull();
      return result;
    }

    @Override
    public void doWrite(byte[] data) {
      // nothing to do
    }
  }
}

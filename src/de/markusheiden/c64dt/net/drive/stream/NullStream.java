package de.markusheiden.c64dt.net.drive.stream;

/**
 * Stream for not used stream.
 */
public class NullStream implements IStream {
  public boolean isOpen() {
    // stream is always closed
    return false;
  }

  public void open(byte[] data) {
  }

  public void incrementPosition(int increment) {
  }

  public byte[] read(int length) {
    return null;
  }

  public void write(byte[] data) {
  }

  public void close() {
  }
}

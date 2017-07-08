package de.heiden.c64dt.net.drive.stream;

/**
 * Stream for not used stream.
 */
public class NullStream implements IStream {
  @Override
  public boolean isOpen() {
    // stream is always closed
    return false;
  }

  @Override
  public void open(byte[] data) {
  }

  @Override
  public void incrementPosition(int increment) {
  }

  @Override
  public byte[] read(int length) {
    return null;
  }

  @Override
  public void write(byte[] data) {
  }

  @Override
  public void close() {
  }
}

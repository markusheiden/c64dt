package main.java.de.heiden.c64dt.net.drive.stream;

import java.io.IOException;

/**
 * A file stream.
 */
public interface IStream {
  /**
   * Is stream open?
   */
  public boolean isOpen();

  /**
   * Open the stream.
   *
   * @param data what to open
   */
  public void open(byte[] data);

  /**
   * Increment buffer position.
   *
   * @param increment increment
   */
  public void incrementPosition(int increment);

  /**
   * Read data from the stream.
   *
   * @param length max. length of the data to read
   */
  public byte[] read(int length) throws IOException;

  /**
   * Write data to the stream.
   */
  public void write(byte[] data) throws IOException;

  /**
   * Close the stream.
   */
  public void close();
}

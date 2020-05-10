package de.heiden.c64dt.net.drive.stream;

import java.io.IOException;

/**
 * A file stream.
 */
public interface IStream {
  /**
   * Is stream open?
   */
  boolean isOpen();

  /**
   * Open the stream.
   *
   * @param data what to open
   */
  void open(byte[] data);

  /**
   * Increment buffer position.
   *
   * @param increment increment
   */
  void incrementPosition(int increment);

  /**
   * Read data from the stream.
   *
   * @param length max. length of the data to read
   */
  byte[] read(int length) throws IOException;

  /**
   * Write data to the stream.
   */
  void write(byte[] data) throws IOException;

  /**
   * Close the stream.
   */
  void close();
}

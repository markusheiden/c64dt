package de.markusheiden.c64dt.net.drive.stream;

import de.markusheiden.c64dt.charset.C64Charset;
import org.springframework.util.Assert;

import java.io.IOException;

/**
 * Base implementation of a stream.
 */
public abstract class AbstractStream implements IStream {
  private boolean isOpen;
  private int lastPosition;
  private int position;

  /**
   * Constructor.
   */
  protected AbstractStream() {
    isOpen = false;
    lastPosition = 0;
    position = 0;
  }

  public boolean isOpen() {
    return isOpen;
  }

  public void open(byte[] data) {
    isOpen = true;
  }

  public final void incrementPosition(int increment) {
    position = lastPosition + increment;
  }

  public final byte[] read(int length) throws IOException {
    Assert.isTrue(isOpen(), "Precondition: isOpen()");
    Assert.isTrue(length >= 0, "Precondition: length >= 0");

    lastPosition = position;
    byte[] result = doRead(length);
    position += result.length;

    Assert.notNull(result, "Postcondition: result != null");
    return result;
  }

  /**
   * Execute read.
   *
   * @param length number of bytes to read
   */
  protected abstract byte[] doRead(int length) throws IOException;

  public final void write(byte[] data) throws IOException {
    Assert.isTrue(isOpen(), "Precondition: isOpen()");
    Assert.notNull(data, "Precondition: data != null");

    doWrite(data);
  }

  /**
   * Limit length to available bytes.
   *
   * @param total total number of bytes
   * @param length requested length
   */
  protected int limitLength(int total, int length) {
    return Math.min(length, total - position);
  }

  /**
   * Execute write.
   *
   * @param data bytes to write
   */
  protected abstract void doWrite(byte[] data) throws IOException;

  public void close() {
    isOpen = false;
  }

  /**
   * Current position.
   */
  public final int getPosition() {
    return position;
  }

  /**
   * Encodes a string to c64 encoding.
   *
   * @param decoded string
   */
  protected static byte[] encode(String decoded) {
    return C64Charset.LOWER.toBytes(decoded);
  }

  /**
   * Decodes a c64 encoded string.
   *
   * @param encoded c64 encoded string
   */
  protected static String decode(byte[] encoded) {
    return C64Charset.LOWER.toString(encoded);
  }
}

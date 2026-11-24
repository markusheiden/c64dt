package de.heiden.c64dt.net.drive.stream;

import java.io.IOException;

import static com.github.cowwoc.requirements10.java.DefaultJavaValidators.requireThat;

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

  @Override
  public boolean isOpen() {
    return isOpen;
  }

  @Override
  public void open(byte[] data) {
    isOpen = true;
  }

  @Override
  public final void incrementPosition(int increment) {
    position = lastPosition + increment;
  }

  @Override
  public final byte[] read(int length) throws IOException {
    requireThat(isOpen(), "isOpen()").isTrue();
    requireThat(length, "length").isGreaterThanOrEqualTo(0);

    lastPosition = position;
    var result = doRead(length);
    position += result.length;

    return result;
  }

  /**
   * Execute read.
   *
   * @param length Number of bytes to read.
   */
  protected abstract byte[] doRead(int length) throws IOException;

  @Override
  public final void write(byte[] data) throws IOException {
    requireThat(isOpen(), "isOpen()").isTrue();

    doWrite(data);
  }

  /**
   * Limit length to available bytes.
   *
   * @param total Total number of bytes.
   * @param length Requested length.
   */
  protected int limitLength(int total, int length) {
    return Math.min(length, total - position);
  }

  /**
   * Execute write.
   *
   * @param data Bytes to write.
   */
  protected abstract void doWrite(byte[] data) throws IOException;

  @Override
  public void close() {
    isOpen = false;
  }

  /**
   * Current position.
   */
  public final int getPosition() {
    return position;
  }
}

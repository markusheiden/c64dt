package de.heiden.c64dt.assembler.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * {@link BufferedWriter} which does not close its output stream.
 * Needed for try-with-resource with {@link System#out}.
 */
public class NonClosingBufferedWriter extends BufferedWriter {
  /**
   * {@inheritDoc}.
   */
  public NonClosingBufferedWriter(Writer out) {
    super(out);
  }

  /**
   * {@inheritDoc}.
   */
  public NonClosingBufferedWriter(Writer out, int sz) {
    super(out, sz);
  }

  @Override
  public void close() throws IOException {
    // just flush, no close!
    flush();
  }
}

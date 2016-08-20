package de.heiden.c64dt.charset;

import java.nio.charset.Charset;

/**
 * Charset for C64 ASCII "PetSCII".
 */
public class PetSCIICharset extends AbstractCharset {
  /**
   * Upper case and graphic chars?
   */
  boolean upper;

  /**
   * Constructor.
   */
  public PetSCIICharset() {
    this(true);
  }

  /**
   * Constructor.
   *
   * @param upper Use upper case charset (true) or lower case charset (false) at start.
   */
  public PetSCIICharset(boolean upper) {
    super("PetSCII");

    this.upper = upper;
  }

  @Override
  public boolean contains(Charset cs) {
    return cs instanceof PetSCIICharset;
  }

  @Override
  public AbstractDecoder newDecoder() {
    return new PetSCIIDecoder(this);
  }

  @Override
  public AbstractEncoder newEncoder() {
    throw new UnsupportedOperationException("Encoding not supported yet");
  }
}

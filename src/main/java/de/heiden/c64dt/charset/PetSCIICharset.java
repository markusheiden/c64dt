package de.heiden.c64dt.charset;

import java.nio.charset.Charset;

/**
 * Charset for C64 ASCII "PetSCII".
 */
public class PetSCIICharset extends AbstractCharset {
  /**
   * Upper case and graphic chars?
   */
  boolean upper = true;

  /**
   * Constructor.
   */
  public PetSCIICharset() {
    super("PetSCII");
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

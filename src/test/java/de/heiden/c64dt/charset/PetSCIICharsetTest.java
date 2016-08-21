package de.heiden.c64dt.charset;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test for {@link PetSCIICharset}
 */
public class PetSCIICharsetTest {
  @Test
  public void decode() throws Exception {
    PetSCIICharset charset = new PetSCIICharset(true);

    assertEquals('A', charset.toChar((byte) 0x41));
    assertEquals('Z', charset.toChar((byte) 0x5A));

    // -> lower case
    assertEquals(0, charset.toChar((byte) 0x0E));

    assertEquals('a', charset.toChar((byte) 0x41));
    assertEquals('z', charset.toChar((byte) 0x5A));
    assertEquals('A', charset.toChar((byte) 0x61));
    assertEquals('Z', charset.toChar((byte) 0x7A));

    // -> upper case
    assertEquals(0, charset.toChar((byte) 0x8E));

    assertEquals('A', charset.toChar((byte) 0x41));
  }
}

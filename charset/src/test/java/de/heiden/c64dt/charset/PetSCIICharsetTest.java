package de.heiden.c64dt.charset;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for {@link PetSCIICharset}.
 */
public class PetSCIICharsetTest {
  @Test
  public void decode() throws Exception {
    var charset = new PetSCIICharset(true);

    assertThat(charset.toChar((byte) 0x41)).isEqualTo('A');
    assertThat(charset.toChar((byte) 0x5A)).isEqualTo('Z');

    // -> Lower case.
    assertThat(charset.toChar((byte) 0x0E)).isEqualTo((char) 0);

    assertThat(charset.toChar((byte) 0x41)).isEqualTo('a');
    assertThat(charset.toChar((byte) 0x5A)).isEqualTo('z');
    assertThat(charset.toChar((byte) 0x61)).isEqualTo('A');
    assertThat(charset.toChar((byte) 0x7A)).isEqualTo('Z');

    // -> Upper case.
    assertThat(charset.toChar((byte) 0x8E)).isEqualTo((char) 0);

    assertThat(charset.toChar((byte) 0x41)).isEqualTo('A');
  }
}

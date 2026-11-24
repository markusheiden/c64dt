package de.heiden.c64dt.charset;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for {@link C64Charset}.
 */
public class C64CharsetTest {
  @Test
  public void encodeLower() {
    assertThat(C64Charset.LOWER.toByte('@')).isEqualTo((byte) 0x00);
    assertThat(C64Charset.LOWER.toByte('a')).isEqualTo((byte) 0x01);
    assertThat(C64Charset.LOWER.toByte('z')).isEqualTo((byte) 0x1A);
    assertThat(C64Charset.LOWER.toByte('A')).isEqualTo((byte) 0x41);
    assertThat(C64Charset.LOWER.toByte('Z')).isEqualTo((byte) 0x5A);

    var hello = new byte[]{0x48, 0x05, 0x0C, 0x0C, 0x0F};
    assertThat(C64Charset.LOWER.toBytes("Hello")).containsExactly(hello);
  }

  @Test
  public void decodeLower() {
    assertThat(C64Charset.LOWER.toChar((byte) 0x00)).isEqualTo('@');
    assertThat(C64Charset.LOWER.toChar((byte) 0x01)).isEqualTo('a');
    assertThat(C64Charset.LOWER.toChar((byte) 0x1A)).isEqualTo('z');
    assertThat(C64Charset.LOWER.toChar((byte) 0x41)).isEqualTo('A');
    assertThat(C64Charset.LOWER.toChar((byte) 0x5A)).isEqualTo('Z');

    var hello = new byte[]{0x48, 0x05, 0x0C, 0x0C, 0x0F};
    assertThat(C64Charset.LOWER.toString(hello)).isEqualTo("Hello");
  }

  @Test
  public void encodeUpper() {
    assertThat(C64Charset.UPPER.toByte('@')).isEqualTo((byte) 0x00);
    assertThat(C64Charset.UPPER.toByte('A')).isEqualTo((byte) 0x01);
    assertThat(C64Charset.UPPER.toByte('Z')).isEqualTo((byte) 0x1A);

    var hello = new byte[]{0x08, 0x05, 0x0C, 0x0C, 0x0F};
    assertThat(C64Charset.LOWER.toBytes("hello")).containsExactly(hello);
  }

  @Test
  public void decodeUpper() {
    assertThat(C64Charset.UPPER.toChar((byte) 0x00)).isEqualTo('@');
    assertThat(C64Charset.UPPER.toChar((byte) 0x01)).isEqualTo('A');
    assertThat(C64Charset.UPPER.toChar((byte) 0x1A)).isEqualTo('Z');

    var hello = new byte[]{0x08, 0x05, 0x0C, 0x0C, 0x0F};
    assertThat(C64Charset.LOWER.toString(hello)).isEqualTo("hello");
  }
}

package de.heiden.c64dt.charset;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Test fpr {@link C64Charset}.
 */
public class C64CharsetTest {
  @Test
  public void encodeLower() {
    assertEquals(0x00, C64Charset.LOWER.toByte('@'));
    assertEquals(0x01, C64Charset.LOWER.toByte('a'));
    assertEquals(0x1A, C64Charset.LOWER.toByte('z'));
    assertEquals(0x41, C64Charset.LOWER.toByte('A'));
    assertEquals(0x5A, C64Charset.LOWER.toByte('Z'));

    byte[] hello = new byte[]{0x48, 0x05, 0x0C, 0x0C, 0x0F};
    assertArrayEquals(hello, C64Charset.LOWER.toBytes("Hello"));
  }

  @Test
  public void decodeLower() {
    assertEquals('@', C64Charset.LOWER.toChar((byte) 0x00));
    assertEquals('a', C64Charset.LOWER.toChar((byte) 0x01));
    assertEquals('z', C64Charset.LOWER.toChar((byte) 0x1A));
    assertEquals('A', C64Charset.LOWER.toChar((byte) 0x41));
    assertEquals('Z', C64Charset.LOWER.toChar((byte) 0x5A));

    byte[] hello = new byte[]{0x48, 0x05, 0x0C, 0x0C, 0x0F};
    assertEquals("Hello", C64Charset.LOWER.toString(hello));
  }

  @Test
  public void encodeUpper() {
    assertEquals(0x00, C64Charset.UPPER.toByte('@'));
    assertEquals(0x01, C64Charset.UPPER.toByte('A'));
    assertEquals(0x1A, C64Charset.UPPER.toByte('Z'));

    byte[] hello = new byte[]{0x08, 0x05, 0x0C, 0x0C, 0x0F};
    assertArrayEquals(hello, C64Charset.LOWER.toBytes("hello"));
  }

  @Test
  public void decodeUpper() {
    assertEquals('@', C64Charset.UPPER.toChar((byte) 0x00));
    assertEquals('A', C64Charset.UPPER.toChar((byte) 0x01));
    assertEquals('Z', C64Charset.UPPER.toChar((byte) 0x1A));

    byte[] hello = new byte[]{0x08, 0x05, 0x0C, 0x0C, 0x0F};
    assertEquals("hello", C64Charset.LOWER.toString(hello));
  }
}

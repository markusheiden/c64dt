package de.heiden.c64dt.bytes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for {@link HexUtil}.
 */
public class HexUtilTest {
  @Test
  public void testHexBytePlain() throws Exception {
    assertEquals("00", HexUtil.hexBytePlain(0x00));
    assertEquals("12", HexUtil.hexBytePlain(0x12));
    assertEquals("FF", HexUtil.hexBytePlain(0xFF));
  }

  @Test
  public void testHexByte() throws Exception {
    assertEquals("$00", HexUtil.hexByte(0x00));
    assertEquals("$12", HexUtil.hexByte(0x12));
    assertEquals("$FF", HexUtil.hexByte(0xFF));
  }

  @Test
  public void testHexWordPlain() throws Exception {
    assertEquals("0000", HexUtil.hexWordPlain(0x0000));
    assertEquals("1234", HexUtil.hexWordPlain(0x1234));
    assertEquals("FFFF", HexUtil.hexWordPlain(0xFFFF));
  }

  @Test
  public void testHexWord() throws Exception {
    assertEquals("$0000", HexUtil.hexWord(0x0000));
    assertEquals("$1234", HexUtil.hexWord(0x1234));
    assertEquals("$FFFF", HexUtil.hexWord(0xFFFF));
  }

  @Test
  public void testHexPlain() throws Exception {
    assertEquals("FFFF", HexUtil.hexPlain(0xFFFF));
    assertEquals("1234", HexUtil.hexPlain(0x1234));
    assertEquals("0100", HexUtil.hexPlain(0x0100));
    assertEquals("FF", HexUtil.hexPlain(0xFF));
    assertEquals("12", HexUtil.hexPlain(0x12));
    assertEquals("00", HexUtil.hexPlain(0x00));
  }

  @Test
  public void testHex() throws Exception {
    assertEquals("$FFFF", HexUtil.hex(0xFFFF));
    assertEquals("$1234", HexUtil.hex(0x1234));
    assertEquals("$0100", HexUtil.hex(0x0100));
    assertEquals("$FF", HexUtil.hex(0xFF));
    assertEquals("$12", HexUtil.hex(0x12));
    assertEquals("$00", HexUtil.hex(0x00));
  }
}

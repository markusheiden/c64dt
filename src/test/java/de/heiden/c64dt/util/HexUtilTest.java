package de.heiden.c64dt.util;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Test for {@link HexUtil}.
 */
public class HexUtilTest
{
  @Test
  public void testHexBytePlain() throws Exception
  {
    assertEquals("12", HexUtil.hexBytePlain(0x12));
  }

  @Test
  public void testHexByte() throws Exception
  {
    assertEquals("$12", HexUtil.hexByte(0x12));
  }

  @Test
  public void testHexWordPlain() throws Exception
  {
    assertEquals("1234", HexUtil.hexWordPlain(0x1234));
  }

  @Test
  public void testHexWord() throws Exception
  {
    assertEquals("$1234", HexUtil.hexWord(0x1234));
  }

  @Test
  public void testHexPlain() throws Exception
  {
    assertEquals("1234", HexUtil.hexPlain(0x1234));
    assertEquals("12", HexUtil.hexPlain(0x12));
  }

  @Test
  public void testHex() throws Exception
  {
    assertEquals("$1234", HexUtil.hex(0x1234));
    assertEquals("$12", HexUtil.hex(0x12));
  }
}

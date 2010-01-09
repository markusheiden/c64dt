package de.heiden.c64dt.util;

import de.heiden.c64dt.util.HexUtil;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case.
 */
public class HexUtil_Test
{
  /**
   * Default test.
   */
  @Test
  public void testDefault ()
  {
    Assert.assertEquals("00", HexUtil.hexBytePlain(0x00));
    Assert.assertEquals("0F", HexUtil.hexBytePlain(0x0F));
    Assert.assertEquals("FF", HexUtil.hexBytePlain(0xFF));
    Assert.assertEquals("0000", HexUtil.hexWordPlain(0x0000));
    Assert.assertEquals("0F00", HexUtil.hexWordPlain(0x0F00));
    Assert.assertEquals("FFFF", HexUtil.hexWordPlain(0xFFFF));
    Assert.assertEquals("$00", HexUtil.hexByte(0x00));
    Assert.assertEquals("$0F", HexUtil.hexByte(0x0F));
    Assert.assertEquals("$FF", HexUtil.hexByte(0xFF));
    Assert.assertEquals("$0000", HexUtil.hexWord(0x0000));
    Assert.assertEquals("$0F00", HexUtil.hexWord(0x0F00));
    Assert.assertEquals("$FFFF", HexUtil.hexWord(0xFFFF));
  }
}

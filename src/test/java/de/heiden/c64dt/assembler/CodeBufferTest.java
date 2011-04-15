package de.heiden.c64dt.assembler;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test for {@link CodeBuffer}.
 */
public class CodeBufferTest
{
  @Test
  public void testHas()
  {
    CodeBuffer buffer = new CodeBuffer(new byte[2]);
    assertTrue(buffer.has(0));
    assertTrue(buffer.has(1));
    assertTrue(buffer.has(2));
    assertFalse(buffer.has(3));
  }

  @Test
  public void testReadByte() {
    CodeBuffer buffer = new CodeBuffer(new byte[]{0x12});
    assertEquals(0x12, buffer.readByte());
    assertFalse(buffer.has(1));

  }

  @Test
  public void testRead() {
    CodeBuffer buffer = new CodeBuffer(new byte[]{0x12, 0x34, 0x56});
    // Read byte
    assertEquals(0x12, buffer.read(1));
    // Read word
    assertEquals(0x5634, buffer.read(2));
    assertFalse(buffer.has(1));
  }
}

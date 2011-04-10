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
    CodeBuffer buffer = new CodeBuffer(0x8000, new byte[2]);
    assertTrue(buffer.has(0));
    assertTrue(buffer.has(1));
    assertTrue(buffer.has(2));
    assertFalse(buffer.has(3));
  }
}

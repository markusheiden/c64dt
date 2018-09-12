package de.heiden.c64dt.assembler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test for {@link CodeBuffer}.
 */
public class CodeBufferTest {
  @Test
  public void testHasMore() {
    CodeBuffer buffer = new CodeBuffer(0, new byte[1]);
    assertTrue(buffer.hasMore());
    buffer.readByte();
    assertFalse(buffer.hasMore());
  }

  @Test
  public void testHas() {
    CodeBuffer buffer = new CodeBuffer(0, new byte[2]);
    assertTrue(buffer.has(0));
    assertTrue(buffer.has(1));
    assertTrue(buffer.has(2));
    assertFalse(buffer.has(3));
  }

  @Test
  public void testReadByte() {
    CodeBuffer buffer = new CodeBuffer(0, new byte[]{0x12});
    assertEquals(0x12, buffer.readByte());
    assertFalse(buffer.has(1));

  }

  @Test
  public void testRead() {
    CodeBuffer buffer = new CodeBuffer(0, new byte[]{0x12, 0x34, 0x56});
    // Read byte
    assertEquals(0x12, buffer.read(1));
    // Read word
    assertEquals(0x5634, buffer.read(2));
    assertFalse(buffer.has(1));
  }
}

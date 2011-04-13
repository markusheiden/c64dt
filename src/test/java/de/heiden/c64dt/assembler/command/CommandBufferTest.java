package de.heiden.c64dt.assembler.command;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test for {@link CommandBuffer}.
 */
public class CommandBufferTest
{
  @Test
  public void testHasAddress() throws Exception
  {
    CommandBuffer commands = new CommandBuffer(0x2000, 0x8000);

    assertFalse(commands.hasAddress(0x7FFF));
    assertTrue(commands.hasAddress(0x8000));
    assertTrue(commands.hasAddress(0x9FFF));
    assertFalse(commands.hasAddress(0xA000));
  }

  @Test
  public void testRebase() throws Exception
  {
    CommandBuffer commands = new CommandBuffer(0x2000, 0x8000);
    commands.rebase(0x1E00, 0xC000);

    assertFalse(commands.hasAddress(0x7FFF));
    assertTrue(commands.hasAddress(0x8000));
    assertTrue(commands.hasAddress(0x9DFF));
    assertFalse(commands.hasAddress(0x9E00));
    assertFalse(commands.hasAddress(0xDDFF));
    assertTrue(commands.hasAddress(0xDE00));
    assertTrue(commands.hasAddress(0xDFFF));
    assertFalse(commands.hasAddress(0xE000));
  }

  @Test
  public void testAddressForIndex() throws Exception
  {
    CommandBuffer commands = new CommandBuffer(0x2000, 0x8000);
    commands.rebase(0x1E00, 0xC000);

    assertEquals(0x8000, commands.addressForIndex(0x0000));
    assertEquals(0x9DFF, commands.addressForIndex(0x1DFF));
    assertEquals(0xDE00, commands.addressForIndex(0x1E00));
    assertEquals(0xDFFF, commands.addressForIndex(0x1FFF));
  }
}

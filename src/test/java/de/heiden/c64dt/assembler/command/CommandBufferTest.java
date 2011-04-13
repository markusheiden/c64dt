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
    CommandBuffer commands = new CommandBuffer(0x10, 0x1000);

    assertFalse(commands.hasAddress(0x0FFF));
    assertTrue(commands.hasAddress(0x1000));
    assertTrue(commands.hasAddress(0x100F));
    assertFalse(commands.hasAddress(0x1010));
  }
}

package de.heiden.c64dt.assembler.command;

import de.heiden.c64dt.assembler.Opcode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link CommandIterator}.
 */
public class CommandIteratorTest
{
  /**
   * Test for
   * {@link CommandIterator#hasNextCommand()}
   * {@link CommandIterator#nextCommand()}.
   */
  @Test
  public void testNextCommand() throws Exception
  {
    CommandBuffer commands = new CommandBuffer(new byte[6], 0x0000);
    // LDA $1234, STA $1234
    commands.addCommand(new OpcodeCommand(Opcode.OPCODE_AD, 0x1234));
    commands.addCommand(new OpcodeCommand(Opcode.OPCODE_8D, 0x1234));

    CommandIterator iter = new CommandIterator(commands);

    assertTrue(iter.hasNextCommand());
    assertEquals(Opcode.OPCODE_AD, ((OpcodeCommand) iter.nextCommand()).getOpcode());
    assertEquals(Opcode.OPCODE_8D, ((OpcodeCommand) iter.nextCommand()).getOpcode());
    assertFalse(iter.hasNextCommand());
  }

  /**
   * Test for
   * {@link CommandIterator#reverse()},
   * {@link CommandIterator#hasPreviousCommand()}
   * {@link CommandIterator#hasPreviousCommand()}.
   */
  @Test
  public void testPreviousCommand() throws Exception
  {
    CommandBuffer commands = new CommandBuffer(new byte[6], 0x0000);
    // LDA $1234, STA $1234
    commands.addCommand(new OpcodeCommand(Opcode.OPCODE_AD, 0x1234));
    commands.addCommand(new OpcodeCommand(Opcode.OPCODE_8D, 0x1234));

    CommandIterator iter = new CommandIterator(commands).reverse();

    assertTrue(iter.hasPreviousCommand());
    assertEquals(Opcode.OPCODE_8D, ((OpcodeCommand) iter.previousCommand()).getOpcode());
    assertEquals(Opcode.OPCODE_AD, ((OpcodeCommand) iter.previousCommand()).getOpcode());
    assertFalse(iter.hasPreviousCommand());
  }
}

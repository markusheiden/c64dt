package de.heiden.c64dt.assembler.command;

import de.heiden.c64dt.assembler.Opcode;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test for {@link CommandIterator}.
 */
public class CommandIteratorTest {
  /**
   * Test for
   * {@link CommandIterator#hasNextCommand()}
   * {@link CommandIterator#nextCommand()}.
   * {@link CommandIterator#peekCommand}.
   * {@link CommandIterator#getIndex()}.
   * {@link CommandIterator#getAddress()}.
   */
  @Test
  public void testNextCommand() throws Exception {
    CommandBuffer commands = new CommandBuffer(new byte[6], 0x1000);
    // LDA $1234, STA $1234
    commands.addCommand(new OpcodeCommand(Opcode.OPCODE_AD, 0x1234));
    commands.addCommand(new OpcodeCommand(Opcode.OPCODE_8D, 0x1234));

    CommandIterator iter = new CommandIterator(commands);

    assertTrue(iter.hasNextCommand());

    // $1000 LDA $1234
    assertEquals(Opcode.OPCODE_AD, ((OpcodeCommand) iter.nextCommand()).getOpcode());
    assertEquals(0x0000, iter.getIndex());
    assertEquals(0x1000, iter.getAddress());

    // Look ahead at next command does not change iterator
    assertEquals(Opcode.OPCODE_8D, ((OpcodeCommand) iter.peekCommand()).getOpcode());
    assertEquals(0x0000, iter.getIndex());
    assertEquals(0x1000, iter.getAddress());

    // $1003 STA $1234
    assertEquals(Opcode.OPCODE_8D, ((OpcodeCommand) iter.nextCommand()).getOpcode());
    assertEquals(0x0003, iter.getIndex());
    assertEquals(0x1003, iter.getAddress());

    assertFalse(iter.hasNextCommand());
  }

  /**
   * Test for
   * {@link CommandIterator#reverse()},
   * {@link CommandIterator#hasPreviousCommand()}
   * {@link CommandIterator#previousCommand()}.
   */
  @Test
  public void testPreviousCommand() throws Exception {
    CommandBuffer commands = new CommandBuffer(new byte[6], 0x1000);
    // LDA $1234, STA $1234
    commands.addCommand(new OpcodeCommand(Opcode.OPCODE_AD, 0x1234));
    commands.addCommand(new OpcodeCommand(Opcode.OPCODE_8D, 0x1234));

    CommandIterator iter = new CommandIterator(commands).reverse();

    assertTrue(iter.hasPreviousCommand());

    // $1003 STA $1234
    assertEquals(Opcode.OPCODE_8D, ((OpcodeCommand) iter.previousCommand()).getOpcode());
    assertEquals(0x0003, iter.getIndex());
    assertEquals(0x1003, iter.getAddress());

    // $1000 LDA $1234
    assertEquals(Opcode.OPCODE_AD, ((OpcodeCommand) iter.previousCommand()).getOpcode());
    assertEquals(0x0000, iter.getIndex());
    assertEquals(0x1000, iter.getAddress());

    assertFalse(iter.hasPreviousCommand());
  }
}

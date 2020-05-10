package de.heiden.c64dt.reassembler.command;

import de.heiden.c64dt.assembler.Opcode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test for {@link CommandIterator}.
 */
public class CommandIteratorTest {
  /**
   * Test for
   * {@link CommandIterator#hasNext()}
   * {@link CommandIterator#next()}.
   * {@link CommandIterator#peek()}.
   * {@link CommandIterator#getIndex()}.
   * {@link CommandIterator#getAddress()}.
   */
  @Test
  public void testNextCommand() throws Exception {
    CommandBuffer commands = new CommandBuffer(new byte[6], 0x1000);
    // LDA $1234, STA $1234
    commands.setCommand(0, new OpcodeCommand(Opcode.OPCODE_AD, 0x1234));
    commands.setCommand(3, new OpcodeCommand(Opcode.OPCODE_8D, 0x1234));

    CommandIterator iter = new CommandIterator(commands);

    assertTrue(iter.hasNext());

    // $1000 LDA $1234
    assertEquals(Opcode.OPCODE_AD, ((OpcodeCommand) iter.next()).getOpcode());
    assertEquals(0x0000, iter.getIndex());
    assertEquals(0x1000, iter.getAddress());

    // Look ahead at next command does not change iterator
    assertEquals(Opcode.OPCODE_8D, ((OpcodeCommand) iter.peek()).getOpcode());
    assertEquals(0x0000, iter.getIndex());
    assertEquals(0x1000, iter.getAddress());

    // $1003 STA $1234
    assertEquals(Opcode.OPCODE_8D, ((OpcodeCommand) iter.next()).getOpcode());
    assertEquals(0x0003, iter.getIndex());
    assertEquals(0x1003, iter.getAddress());

    assertFalse(iter.hasNext());
  }

  /**
   * Test for
   * {@link CommandIterator#reverse()},
   * {@link CommandIterator#hasPrevious()}
   * {@link CommandIterator#previous()}.
   */
  @Test
  public void testPreviousCommand() throws Exception {
    CommandBuffer commands = new CommandBuffer(new byte[6], 0x1000);
    // LDA $1234, STA $1234
    commands.setCommand(0, new OpcodeCommand(Opcode.OPCODE_AD, 0x1234));
    commands.setCommand(3, new OpcodeCommand(Opcode.OPCODE_8D, 0x1234));

    CommandIterator iter = new CommandIterator(commands).reverse();

    assertTrue(iter.hasPrevious());

    // $1003 STA $1234
    assertEquals(Opcode.OPCODE_8D, ((OpcodeCommand) iter.previous()).getOpcode());
    assertEquals(0x0003, iter.getIndex());
    assertEquals(0x1003, iter.getAddress());

    // $1000 LDA $1234
    assertEquals(Opcode.OPCODE_AD, ((OpcodeCommand) iter.previous()).getOpcode());
    assertEquals(0x0000, iter.getIndex());
    assertEquals(0x1000, iter.getAddress());

    assertFalse(iter.hasPrevious());
  }
}

package de.heiden.c64dt.reassembler.command;

import de.heiden.c64dt.assembler.Opcode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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

    assertThat(iter.hasNext()).isTrue();

    // $1000 LDA $1234
    assertThat(((OpcodeCommand) iter.next()).getOpcode()).isEqualTo(Opcode.OPCODE_AD);
    assertThat(iter.getIndex()).isEqualTo(0x0000);
    assertThat(iter.getAddress()).isEqualTo(0x1000);

    // Look ahead at next command does not change iterator.
    assertThat(((OpcodeCommand) iter.peek()).getOpcode()).isEqualTo(Opcode.OPCODE_8D);
    assertThat(iter.getIndex()).isEqualTo(0x0000);
    assertThat(iter.getAddress()).isEqualTo(0x1000);

    // $1003 STA $1234
    assertThat(((OpcodeCommand) iter.next()).getOpcode()).isEqualTo(Opcode.OPCODE_8D);
    assertThat(iter.getIndex()).isEqualTo(0x0003);
    assertThat(iter.getAddress()).isEqualTo(0x1003);

    assertThat(iter.hasNext()).isFalse();
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

    assertThat(iter.hasPrevious()).isTrue();

    // $1003 STA $1234
    assertThat(((OpcodeCommand) iter.previous()).getOpcode()).isEqualTo(Opcode.OPCODE_8D);
    assertThat(iter.getIndex()).isEqualTo(0x0003);
    assertThat(iter.getAddress()).isEqualTo(0x1003);

    // $1000 LDA $1234
    assertThat(((OpcodeCommand) iter.previous()).getOpcode()).isEqualTo(Opcode.OPCODE_AD);
    assertThat(iter.getIndex()).isEqualTo(0x0000);
    assertThat(iter.getAddress()).isEqualTo(0x1000);

    assertThat(iter.hasPrevious()).isFalse();
  }
}

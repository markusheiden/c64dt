package de.heiden.c64dt.reassembler.command;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for {@link CommandBuffer}.
 */
public class CommandBufferTest {
  @Test
  public void testHasAddress() throws Exception {
    var commands = new CommandBuffer(new byte[0x2000], 0x8000);

    assertThat(commands.hasAddress(0x7FFF)).isFalse();
    assertThat(commands.hasAddress(0x8000)).isTrue();
    assertThat(commands.hasAddress(0x9FFF)).isTrue();
    assertThat(commands.hasAddress(0xA000)).isFalse();
  }

  @Test
  public void testRebase() throws Exception {
    var commands = new CommandBuffer(new byte[0x2000], 0x8000);
    commands.rebase(0x1E00, 0xC000);

    assertThat(commands.hasAddress(0x7FFF)).isFalse();
    assertThat(commands.hasAddress(0x8000)).isTrue();
    assertThat(commands.hasAddress(0x9DFF)).isTrue();
    assertThat(commands.hasAddress(0x9E00)).isFalse();
    assertThat(commands.hasAddress(0xDDFF)).isFalse();
    assertThat(commands.hasAddress(0xDE00)).isTrue();
    assertThat(commands.hasAddress(0xDFFF)).isTrue();
    assertThat(commands.hasAddress(0xE000)).isFalse();
  }

  @Test
  public void testAddressForIndex() throws Exception {
    var commands = new CommandBuffer(new byte[0x2000], 0x8000);
    commands.rebase(0x1E00, 0xC000);

    assertThat(commands.addressForIndex(0x0000)).isEqualTo(0x8000);
    assertThat(commands.addressForIndex(0x1DFF)).isEqualTo(0x9DFF);
    assertThat(commands.addressForIndex(0x1E00)).isEqualTo(0xDE00);
    assertThat(commands.addressForIndex(0x1FFF)).isEqualTo(0xDFFF);
  }
}

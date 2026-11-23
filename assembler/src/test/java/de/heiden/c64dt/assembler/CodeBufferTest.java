package de.heiden.c64dt.assembler;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for {@link CodeBuffer}.
 */
public class CodeBufferTest {
  @Test
  public void testHasMore() {
    CodeBuffer buffer = new CodeBuffer(0, new byte[1]);
    assertThat(buffer.hasMore()).isTrue();
    buffer.readByte();
    assertThat(buffer.hasMore()).isFalse();
  }

  @Test
  public void testHas() {
    CodeBuffer buffer = new CodeBuffer(0, new byte[2]);
    assertThat(buffer.has(0)).isTrue();
    assertThat(buffer.has(1)).isTrue();
    assertThat(buffer.has(2)).isTrue();
    assertThat(buffer.has(3)).isFalse();
  }

  @Test
  public void testReadByte() {
    CodeBuffer buffer = new CodeBuffer(0, new byte[]{0x12});
    assertThat(buffer.readByte()).isEqualTo(0x12);
    assertThat(buffer.has(1)).isFalse();

  }

  @Test
  public void testRead() {
    CodeBuffer buffer = new CodeBuffer(0, new byte[]{0x12, 0x34, 0x56});
    // Read byte.
    assertThat(buffer.read(1)).isEqualTo(0x12);
    // Read word.
    assertThat(buffer.read(2)).isEqualTo(0x5634);
    assertThat(buffer.has(1)).isFalse();
  }
}

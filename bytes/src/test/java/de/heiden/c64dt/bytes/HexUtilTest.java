package de.heiden.c64dt.bytes;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for {@link HexUtil}.
 */
public class HexUtilTest {
  @Test
  public void testHexBytePlain() throws Exception {
    assertThat(HexUtil.hexBytePlain(0x00)).isEqualTo("00");
    assertThat(HexUtil.hexBytePlain(0x12)).isEqualTo("12");
    assertThat(HexUtil.hexBytePlain(0xFF)).isEqualTo("FF");
  }

  @Test
  public void testHexByte() throws Exception {
    assertThat(HexUtil.hexByte(0x00)).isEqualTo("$00");
    assertThat(HexUtil.hexByte(0x12)).isEqualTo("$12");
    assertThat(HexUtil.hexByte(0xFF)).isEqualTo("$FF");
  }

  @Test
  public void testHexWordPlain() throws Exception {
    assertThat(HexUtil.hexWordPlain(0x0000)).isEqualTo("0000");
    assertThat(HexUtil.hexWordPlain(0x1234)).isEqualTo("1234");
    assertThat(HexUtil.hexWordPlain(0xFFFF)).isEqualTo("FFFF");
  }

  @Test
  public void testHexWord() throws Exception {
    assertThat(HexUtil.hexWord(0x0000)).isEqualTo("$0000");
    assertThat(HexUtil.hexWord(0x1234)).isEqualTo("$1234");
    assertThat(HexUtil.hexWord(0xFFFF)).isEqualTo("$FFFF");
  }

  @Test
  public void testHexPlain() throws Exception {
    assertThat(HexUtil.hexPlain(0xFFFF)).isEqualTo("FFFF");
    assertThat(HexUtil.hexPlain(0x1234)).isEqualTo("1234");
    assertThat(HexUtil.hexPlain(0x0100)).isEqualTo("0100");
    assertThat(HexUtil.hexPlain(0xFF)).isEqualTo("FF");
    assertThat(HexUtil.hexPlain(0x12)).isEqualTo("12");
    assertThat(HexUtil.hexPlain(0x00)).isEqualTo("00");
  }

  @Test
  public void testHex() throws Exception {
    assertThat(HexUtil.hex(0xFFFF)).isEqualTo("$FFFF");
    assertThat(HexUtil.hex(0x1234)).isEqualTo("$1234");
    assertThat(HexUtil.hex(0x0100)).isEqualTo("$0100");
    assertThat(HexUtil.hex(0xFF)).isEqualTo("$FF");
    assertThat(HexUtil.hex(0x12)).isEqualTo("$12");
    assertThat(HexUtil.hex(0x00)).isEqualTo("$00");
  }
}

package de.heiden.c64dt.reassembler.label;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for {@link AbstractLabel}.
 */
public class AbstractLabelTest {
  @Test
  public void testGetAddress() throws Exception {
    var label = new DataLabel(0x1234);

    assertThat(label.getAddress()).isEqualTo(0x1234);
  }

  @Test
  public void testCompareTo() throws Exception {
    var label1c = new CodeLabel(1);
    var label1d = new DataLabel(1);
    var label2d = new DataLabel(2);

    assertThat(label1c.compareTo(label2d)).isNegative();
    assertThat(label2d.compareTo(label1c)).isPositive();
    assertThat(label1c.compareTo(label1d)).isZero();
    assertThat(label1c.compareTo(label1c)).isZero();
  }

  @Test
  public void testToString() throws Exception {
    var label = new DataLabel(0x1234);

    assertThat(label.toString(0x1234)).isEqualTo("l_1234");
    assertThat(label.toString(0x1234 + 1)).isEqualTo("l_1234 + 1");
    assertThat(label.toString(0x1234 - 1)).isEqualTo("l_1234 - 1");
  }
}

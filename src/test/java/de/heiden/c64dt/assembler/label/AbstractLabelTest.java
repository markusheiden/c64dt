package de.heiden.c64dt.assembler.label;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test for {@link AbstractLabel}.
 */
public class AbstractLabelTest
{
  @Test
  public void testGetAddress() throws Exception
  {
    AbstractLabel label = new DataLabel(0x1234);

    assertEquals(0x1234, label.getAddress());
  }

  @Test
  public void testCompareTo() throws Exception
  {
    AbstractLabel label1c = new CodeLabel(1);
    AbstractLabel label1d = new DataLabel(1);
    AbstractLabel label2d = new DataLabel(2);

    assertTrue(label1c.compareTo(label2d) < 0);
    assertTrue(label2d.compareTo(label1c) > 0);
    assertTrue(label1c.compareTo(label1d) == 0);
    assertTrue(label1c.compareTo(label1c) == 0);
  }

  @Test
  public void testToString() throws Exception
  {
    AbstractLabel label = new DataLabel(0x1234);

    assertEquals("l_1234", label.toString(0x1234));
    assertEquals("l_1234 + 1", label.toString(0x1234 + 1));
    assertEquals("l_1234 - 1", label.toString(0x1234 - 1));
  }
}

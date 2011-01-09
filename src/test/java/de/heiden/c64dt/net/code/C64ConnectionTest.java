package de.heiden.c64dt.net.code;

import de.heiden.c64dt.net.code.C64Connection;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;

import static de.heiden.c64dt.util.ByteUtil.hi;
import static de.heiden.c64dt.util.ByteUtil.lo;
import static junit.framework.Assert.assertEquals;

/**
 * Test for {@linkC64ConnectionTest}.
 */
public class C64ConnectionTest
{
  @Test
  public void testFill() throws Exception
  {
    TestConnection connection = new TestConnection();
    connection.open();
    connection.fill(0x1234, 0x0102, (byte) 6);
    // 0, 1: Magic
    // 2   : Sequence (starts with 1)
    // 3   : Service (5: Fill)
    // 4, 5: Address (hi, lo)
    // 6, 7: Length (hi, lo)
    // 8   : Fill byte
    // 9   : Padding
    connection.assertSentData(0xCA, 0x1F, 1, 5, 0x12, 0x34, 0x01, 0x02, 0x06, 0x00);
    connection.close();
  }

  private static class TestConnection extends C64Connection
  {
    private int length = -1;

    public TestConnection() throws IOException
    {
      super(InetAddress.getLocalHost());
    }

    @Override
    public void sendPacket(int length) throws IOException
    {
      this.length = length;
      super.sendPacket(length);
    }

    public void assertSentData(int... data)
    {
      assertEquals(length, data.length);
      for (int i = 0; i < data.length; i++)
      {
        assertEquals("Byte " + i, (byte) data[i], output[i]);
      }
    }
  }
}

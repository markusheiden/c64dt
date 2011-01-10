package de.heiden.c64dt.net.code;

import de.heiden.c64dt.net.Packet;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;

import static junit.framework.Assert.assertEquals;

/**
 * Test for {@linkC64ConnectionTest}.
 */
public class C64ConnectionTest
{
  @Test
  public void testPing() throws Exception
  {
    TestConnection connection = new TestConnection();
    connection.open();
    connection.ping();
    // 0, 1: Magic
    // 2   : Sequence (starts with 1)
    // 3   : Service (0: Ping)
    assertSentData(0xCA, 0x1F, 1, 0);
    connection.close();
  }

  @Test
  public void testWrite() throws Exception
  {
    TestConnection connection = new TestConnection();
    connection.open();
    connection.write(0x1234, (byte) 5, (byte) 6, (byte) 7);
    // 0, 1: Magic
    // 2   : Sequence (starts with 1)
    // 3   : Service (4: Write)
    // 4, 5: Address (hi, lo)
    // 6, 7: Length (hi, lo)
    // 8..A: Data
    // B   : Padding
    assertSentData(0xCA, 0x1F, 1, 4, 0x12, 0x34, 0x00, 0x03, 5, 6, 7, 0x00);
    connection.close();
  }

  @Test
  public void testFill() throws Exception
  {
    TestConnection connection = new TestConnection();
    connection.open();
    connection.fill(0x1234, 0x0102, 6);
    // 0, 1: Magic
    // 2   : Sequence (starts with 1)
    // 3   : Service (5: Fill)
    // 4, 5: Address (hi, lo)
    // 6, 7: Length (hi, lo)
    // 8   : Fill byte
    // 9   : Padding
    assertSentData(0xCA, 0x1F, 1, 5, 0x12, 0x34, 0x01, 0x02, 0x06, 0x00);
    connection.close();
  }

  @Test
  public void testJump() throws Exception
  {
    TestConnection connection = new TestConnection();
    connection.open();
    connection.jump(0x1234);
    // 0, 1: Magic
    // 2   : Sequence (starts with 1)
    // 3   : Service (6: Jump)
    // 4, 5: Address (hi, lo)
    assertSentData(0xCA, 0x1F, 1, 6, 0x12, 0x34);
    connection.close();
  }

  @Test
  public void testRun() throws Exception
  {
    TestConnection connection = new TestConnection();
    connection.open();
    connection.run();
    // 0, 1: Magic
    // 2   : Sequence (starts with 1)
    // 3   : Service (7: Fill)
    assertSentData(0xCA, 0x1F, 1, 7);
    connection.close();
  }

  public void assertSentData(int... expected)
  {
    assertEquals(expected.length, outputData.length);
    for (int i = 0; i < expected.length; i++)
    {
      assertEquals("Byte " + i, (byte) expected[i], outputData[i]);
    }
  }

  private byte[] outputData;

  private class TestConnection extends C64Connection
  {
    public TestConnection() throws IOException
    {
      super(InetAddress.getLocalHost());
    }

    @Override
    public void sendPacket(Packet packet) throws IOException
    {
      super.sendPacket(packet);
      // Capture after sending, because packet may have been padded now
      outputData = packet.getData();
    }

    @Override
    public Packet receivePacket() throws IOException
    {
      // Always acknowledge OK
      Packet ack = new Packet(4);
      ack.addByte(0xCA, 0x1F, 1, 1); // ACK OK

      return ack;
    }
  }
}

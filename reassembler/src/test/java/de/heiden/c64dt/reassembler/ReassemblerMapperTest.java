package de.heiden.c64dt.reassembler;

/**
 * Test for {@link ReassemblerMapper}.
 */
public class ReassemblerMapperTest {
/*
  @Test
  public void testWriteRead() throws Exception
  {
    // code
    byte[] code = new byte[0x0100];
    for (int i = 0; i < code.length; i++)
    {
      code[i] = (byte) i;
    }

    CommandBuffer commands = new CommandBuffer(code, 0x1000);
    Reassembler reassembler = new Reassembler();
    reassembler.reassemble(commands);

    ReassemblerMapper mapper = new ReassemblerMapper();

    // write to xml
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    mapper.write(reassembler, os);
    byte[] xml = os.toByteArray();

    // read written xml
    Reassembler read = mapper.read(new ByteArrayInputStream(xml));

    List<IDetector> detectors = reassembler.getDetectors();
    List<IDetector> readDetectors = read.getDetectors();
    for (int i = 0; i < detectors.size(); i++)
    {
      assertEquals(detectors.get(i).getClass(), readDetectors.get(i).getClass());
    }

    // write read xml again
    os.reset();
    mapper.write(read, os);
    byte[] readXml = os.toByteArray();

    assertEquals(xml.length, readXml.length);
    for (int i = 0; i < xml.length; i++)
    {
      assertEquals("Byte " + i, xml[i], readXml[i]);
    }
  }
*/
}

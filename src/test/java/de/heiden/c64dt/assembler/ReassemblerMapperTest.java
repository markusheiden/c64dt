package de.heiden.c64dt.assembler;

import de.heiden.c64dt.assembler.command.CommandBuffer;
import de.heiden.c64dt.assembler.command.CommandBufferMapper;
import de.heiden.c64dt.assembler.detector.IDetector;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.PrintWriter;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Test for {@link ReassemblerMapper}.
 */
public class ReassemblerMapperTest
{
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

    Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

    // write test command buffer
    mapper.write(reassembler, document);
    // read written xml
    Reassembler read = mapper.read(document);

    List<IDetector> reassemblerDetectors = reassembler.getDetectors();
    List<IDetector> readDetectors = read.getDetectors();
    for (int i = 0; i < reassemblerDetectors.size(); i++)
    {
      assertEquals(reassemblerDetectors.get(i).getClass(), readDetectors.get(i).getClass());
    }
  }
}

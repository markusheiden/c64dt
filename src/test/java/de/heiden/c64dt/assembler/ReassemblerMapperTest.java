package de.heiden.c64dt.assembler;

import de.heiden.c64dt.assembler.command.CommandBuffer;
import de.heiden.c64dt.assembler.command.CommandBufferMapper;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.PrintWriter;

/**
 * Test for {@link ReassemblerMapper}.
 */
public class ReassemblerMapperTest
{
  @Test
  public void testWriteRead() throws Exception
  {
    CommandBuffer commands = new CommandBuffer(new byte[0], 0x1000);
    Reassembler reassembler = new Reassembler();
    reassembler.reassemble(commands);

    ReassemblerMapper mapper = new ReassemblerMapper();

    Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

    // write test command buffer
    mapper.write(reassembler, document);
    // read written xml
    Reassembler read = mapper.read(document);
  }
}

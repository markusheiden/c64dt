package de.heiden.c64dt.assembler.command;

import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.util.XmlUtil;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;

import static junit.framework.Assert.*;

/**
 */
public class CommandBufferMapperTest
{
  @Test
  public void testWriteRead() throws Exception
  {
    // code
    byte[] code = new byte[0xC000];
    for (int i = 0; i < code.length; i++)
    {
      code[i] = (byte) i;
    }

    CommandBuffer commands = new CommandBuffer(code, 0x1000);

    // addresses
    commands.rebase(0x0010, 0x2000);
    commands.rebase(0x0020, 0x8000);
    commands.rebase(0x0030, 0xF000);

    // code types
    commands.setType(0x0000, 0x9000, CodeType.DATA);
    commands.setType(0x9000, CodeType.OPCODE);
    commands.setType(0x9001, 0xA000, CodeType.CODE);

    CommandBufferMapper mapper = new CommandBufferMapper();

    Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    Element parent = document.createElement("test");
    document.appendChild(parent);

    mapper.write(commands, document, parent);

    // debug
    XmlUtil.toStream(document, System.out);

    CommandBuffer read = mapper.read((Element) parent.getElementsByTagName(CommandBufferMapper.ELEMENT).item(0));

    // code
    for (int i = 0; i < code.length; i++)
    {
      assertEquals("Byte " + i + ":", code[i], read.getCode()[i]);
    }

    // code types
    for (int index = 0; index < commands.getLength(); index++)
    {
      assertEquals("Index " + index + ":", commands.getType(index), read.getType(index));
    }

    // addresses
    for (Integer index : commands.getStartAddresses().keySet())
    {
      assertEquals("Index " + index + ":", commands.getStartAddresses().get(index), read.getStartAddresses().get(index));
    }
  }
}

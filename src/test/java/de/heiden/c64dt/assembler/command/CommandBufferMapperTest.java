package de.heiden.c64dt.assembler.command;

import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.assembler.Reassembler;
import de.heiden.c64dt.assembler.detector.IDetector;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.SortedMap;

import static junit.framework.Assert.assertEquals;

/**
 * Test for {@link CommandBufferMapper}.
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

    // subroutines
    commands.addSubroutine(0x0010, 0x10);
    commands.addSubroutine(0x0020, 0x20);
    commands.addSubroutine(0x0030, 0x30);

    // code types
    commands.setType(0x0000, 0x9000, CodeType.DATA);
    commands.setType(0x9000, CodeType.OPCODE);
    commands.setType(0x9001, 0xA000, CodeType.CODE);

    CommandBufferMapper mapper = new CommandBufferMapper();

    // write to xml
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    mapper.write(commands, os);
    byte[] xml = os.toByteArray();

    // read written xml
    CommandBuffer read = mapper.read(new ByteArrayInputStream(xml));

    // code
    byte[] readCode = read.getCode();
    for (int i = 0; i < code.length; i++)
    {
      assertEquals("Byte " + i + ":", code[i], readCode[i]);
    }

    // code types
    for (int index = 0; index < commands.getLength(); index++)
    {
      assertEquals("Index " + index + ":", commands.getType(index), read.getType(index));
    }

    // addresses
    SortedMap<Integer, Integer> commandsStart = commands.getStartAddresses();
    SortedMap<Integer, Integer> readStart = read.getStartAddresses();
    for (Integer index : commandsStart.keySet())
    {
      assertEquals("Index " + index + ":", commandsStart.get(index), readStart.get(index));
    }

    // subroutines
    SortedMap<Integer, Integer> commandsSubroutines = commands.getStartAddresses();
    SortedMap<Integer, Integer> readSubroutines = read.getStartAddresses();
    for (Integer index : commandsSubroutines.keySet())
    {
      assertEquals("Index " + index + ":", commandsSubroutines.get(index), readSubroutines.get(index));
    }

    // write read xml again
    os.reset();
    mapper.write(read, os);
    byte[] readXml = os.toByteArray();

    assertEquals(xml.length, readXml.length);
    for (int i = 0; i < xml.length; i++)
    {
      assertEquals("Byte " + i,  xml[i], readXml[i]);
    }
  }
}

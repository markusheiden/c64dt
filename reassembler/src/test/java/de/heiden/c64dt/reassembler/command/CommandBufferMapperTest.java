package de.heiden.c64dt.reassembler.command;

import de.heiden.c64dt.assembler.CodeBuffer;
import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.reassembler.Reassembler;
import de.heiden.c64dt.reassembler.xml.XmlUtil;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.SortedMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for {@link CommandBufferMapper}.
 */
public class CommandBufferMapperTest {
  @Test
  public void testWriteRead() throws Exception {
    // code
    byte[] code = new byte[0xC000];
    for (int i = 0; i < code.length; i++) {
      code[i] = (byte) i;
    }

    Reassembler reassembler = new Reassembler();
    reassembler.reassemble(new CodeBuffer(0x1000, code));
    CommandBuffer commands = reassembler.getCommands();

    // addresses
    commands.rebase(0x0010, 0x2000);
    commands.rebase(0x0020, 0x8000);
    commands.rebase(0x0030, 0xF000);

    // subroutines
    commands.addSubroutine(new Subroutine(0x1000, 0x10));
    commands.addSubroutine(new Subroutine(0x2000, 0x20));
    commands.addSubroutine(new Subroutine(0x3000, 0x30));

    // code types
    commands.setType(0x0000, 0x9000, CodeType.DATA);
    commands.setType(0x9000, CodeType.OPCODE);
    commands.setType(0x9001, 0xA000, CodeType.CODE);

    // write to xml
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    XmlUtil.marshal(reassembler, os);
    byte[] xml = os.toByteArray();

    // read written xml
    Reassembler readReassembler = XmlUtil.unmarshal(new ByteArrayInputStream(xml), Reassembler.class);
    CommandBuffer readCommands = readReassembler.getCommands();

    // code
    byte[] readCode = readCommands.getCode();
    for (int i = 0; i < code.length; i++) {
      assertEquals(code[i], readCode[i], "Byte " + i + ":");
    }

    // code types
    for (int index = 0; index < commands.getLength(); index++) {
      assertEquals(commands.getType(index), readCommands.getType(index), "Index " + index + ":");
    }

    // addresses
    SortedMap<Integer, Integer> commandsStart = commands.getStartAddresses();
    SortedMap<Integer, Integer> readStart = readCommands.getStartAddresses();
    for (Integer index : commandsStart.keySet()) {
      assertEquals(commandsStart.get(index), readStart.get(index), "Index " + index + ":");
    }

    // subroutines
    SortedMap<Integer, Integer> commandsSubroutines = commands.getStartAddresses();
    SortedMap<Integer, Integer> readSubroutines = readCommands.getStartAddresses();
    for (Integer address : commandsSubroutines.keySet()) {
      assertEquals(commandsSubroutines.get(address), readSubroutines.get(address), "Address " + address + ":");
    }

    // write read xml again
    os.reset();
    XmlUtil.marshal(readReassembler, os);
    byte[] readXml = os.toByteArray();

    assertEquals(xml.length, readXml.length);
    for (int i = 0; i < xml.length; i++) {
      assertEquals(xml[i], readXml[i], "Byte " + i);
    }
  }
}

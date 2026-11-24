package de.heiden.c64dt.reassembler.command;

import de.heiden.c64dt.assembler.CodeBuffer;
import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.reassembler.Reassembler;
import de.heiden.c64dt.reassembler.xml.XmlUtil;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.SortedMap;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for {@link CommandBufferMapper}.
 */
public class CommandBufferMapperTest {
  @Test
  public void testWriteRead() throws Exception {
    // Code.
    var code = new byte[0xC000];
    for (var i = 0; i < code.length; i++) {
      code[i] = (byte) i;
    }

    var reassembler = new Reassembler();
    reassembler.reassemble(new CodeBuffer(0x1000, code));
    var commands = reassembler.getCommands();

    // Addresses.
    commands.rebase(0x0010, 0x2000);
    commands.rebase(0x0020, 0x8000);
    commands.rebase(0x0030, 0xF000);

    // Subroutines.
    commands.addSubroutine(new Subroutine(0x1000, 0x10));
    commands.addSubroutine(new Subroutine(0x2000, 0x20));
    commands.addSubroutine(new Subroutine(0x3000, 0x30));

    // Code types.
    commands.setType(0x0000, 0x9000, CodeType.DATA);
    commands.setType(0x9000, CodeType.OPCODE);
    commands.setType(0x9001, 0xA000, CodeType.CODE);

    // Write to XML.
    var os = new ByteArrayOutputStream();
    XmlUtil.marshal(reassembler, os);
    var xml = os.toByteArray();

    // Read written XML.
    var readReassembler = XmlUtil.unmarshal(new ByteArrayInputStream(xml), Reassembler.class);
    var readCommands = readReassembler.getCommands();

    // Code.
    var readCode = readCommands.getCode();
    for (var i = 0; i < code.length; i++) {
      assertThat(readCode[i]).as("Byte " + i + ":").isEqualTo(code[i]);
    }

    // Code types.
    for (var index = 0; index < commands.getLength(); index++) {
      assertThat(readCommands.getType(index)).as("Index " + index + ":").isEqualTo(commands.getType(index));
    }

    // Addresses.
    var commandsStart = commands.getStartAddresses();
    var readStart = readCommands.getStartAddresses();
    for (var index : commandsStart.keySet()) {
      assertThat(readStart.get(index)).as("Index " + index + ":").isEqualTo(commandsStart.get(index));
    }

    // Subroutines.
    var commandsSubroutines = commands.getStartAddresses();
    var readSubroutines = readCommands.getStartAddresses();
    for (var address : commandsSubroutines.keySet()) {
      assertThat(readSubroutines.get(address)).as("Address " + address + ":").isEqualTo(commandsSubroutines.get(address));
    }

    // Write read XML again.
    os.reset();
    XmlUtil.marshal(readReassembler, os);
    var readXml = os.toByteArray();

    assertThat(readXml.length).isEqualTo(xml.length);
    for (var i = 0; i < xml.length; i++) {
      assertThat(readXml[i]).as("Byte " + i).isEqualTo(xml[i]);
    }
  }
}

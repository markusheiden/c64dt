package de.heiden.c64dt;

import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.assembler.Reassembler;
import de.heiden.c64dt.assembler.command.Subroutine;
import de.heiden.c64dt.util.XmlUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class MapperTest {
  public static void main(String[] args) throws Exception {
    Reassembler reassembler = new Reassembler();
    reassembler.reassemble(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
    reassembler.getCommands().setType(1, 3, CodeType.CODE);
    reassembler.getCommands().addSubroutine(new Subroutine(4, 2));

    // TODO mh: Write to System.out
    XmlUtil.marshal(reassembler, null);

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    XmlUtil.marshal(reassembler, os);
    Reassembler read = XmlUtil.unmarshal(new ByteArrayInputStream(os.toByteArray()), Reassembler.class);

    // TODO mh: Write to System.out
    XmlUtil.marshal(read, System.out);
  }
}

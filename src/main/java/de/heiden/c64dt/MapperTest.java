package de.heiden.c64dt;

import com.sun.org.apache.xpath.internal.axes.WalkingIterator;
import com.sun.servicetag.SystemEnvironment;
import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.assembler.Reassembler;
import de.heiden.c64dt.assembler.ReassemblerMapper;

import javax.xml.bind.JAXB;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

public class MapperTest
{
  public static void main(String[] args) throws Exception
  {
    ReassemblerMapper mapper = new ReassemblerMapper();
    Reassembler reassembler = new Reassembler();
    reassembler.reassemble(new byte[]{1,2,3,4,5,6,7,8,9,10});
    reassembler.getCommands().setType(1, 3, CodeType.CODE);
    reassembler.getCommands().addSubroutine(4, 2);

    mapper.write(reassembler, System.out);

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    mapper.write(reassembler, os);
    Reassembler read = mapper.read(new ByteArrayInputStream(os.toByteArray()));

    mapper.write(read, System.out);
  }
}

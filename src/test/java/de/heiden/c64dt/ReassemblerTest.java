package de.heiden.c64dt;

import de.heiden.c64dt.assembler.Disassembler;
import de.heiden.c64dt.assembler.Reassembler;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;

/**
 * Reassembler test startup.
 */
public class ReassemblerTest
{
  public static void main(String[] args) throws Exception
  {
    disassemble();
  }

  public static void disassemble() throws Exception
  {
    byte[] data = FileCopyUtils.copyToByteArray(new File("./dummy2.prg"));
    Disassembler disassembler = new Disassembler();
    disassembler.disassemble(data, new OutputStreamWriter(System.out));
  }

  public static void reassemble() throws Exception
  {
    byte[] data = FileCopyUtils.copyToByteArray(new File("./dummy2.prg"));
    Reassembler reassembler = new Reassembler();
    reassembler.reassemble(data);
  }

  public static void reassemblerPerformanceTest() throws Exception
  {
    byte[] data = FileCopyUtils.copyToByteArray(new File("./dummy2.prg"));
    Reassembler reassembler = new Reassembler();
    for (int i = 0; i < 64; i++)
    {
      reassembler.reassemble(data);
    }
  }

  public static void strip() throws IOException
  {
    byte[] data = FileCopyUtils.copyToByteArray(new File("./dummy.prg"));
    byte[] result = new byte[data.length - 0xFF];
    result[0] = 0x00;
    result[1] = 0x50;
    System.arraycopy(data, 0xFF + 2, result, 2, result.length - 2);
    FileCopyUtils.copy(result, new File("./dummy2.prg"));
  }
}

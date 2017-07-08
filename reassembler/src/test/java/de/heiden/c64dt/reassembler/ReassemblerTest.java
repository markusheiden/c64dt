package de.heiden.c64dt.reassembler;

import de.heiden.c64dt.assembler.CodeBuffer;
import de.heiden.c64dt.assembler.Disassembler;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Reassembler test startup.
 */
public class ReassemblerTest {
  public static void main(String[] args) throws Exception {
    disassemble();
  }

  public static void disassemble() throws Exception {
    new Disassembler().disassemble(
      CodeBuffer.fromProgram(new FileInputStream("./dummy2.prg")),
      new OutputStreamWriter(System.out));
  }

  public static void reassemble() throws Exception {
    Reassembler reassembler = new Reassembler();
    reassembler.reassemble(CodeBuffer.fromProgram(new FileInputStream("./dummy2.prg")));
  }

  public static void reassemblerPerformanceTest() throws Exception {
    byte[] data = FileCopyUtils.copyToByteArray(new File("./dummy2.prg"));
    Reassembler reassembler = new Reassembler();
    for (int i = 0; i < 64; i++) {
      reassembler.reassemble(CodeBuffer.fromProgram(data));
    }
  }

  public static void strip() throws IOException {
    byte[] data = FileCopyUtils.copyToByteArray(new File("./dummy.prg"));
    byte[] result = new byte[data.length - 0xFF];
    result[0] = 0x00;
    result[1] = 0x50;
    System.arraycopy(data, 0xFF + 2, result, 2, result.length - 2);
    FileCopyUtils.copy(result, new File("./dummy2.prg"));
  }
}

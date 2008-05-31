package de.markusheiden.c64dt;

import de.markusheiden.c64dt.assembler.Reassembler;
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
    new Reassembler().reassemble(new FileInputStream("./dummy2.prg"), new OutputStreamWriter(System.out));
  }

  private static void strip() throws IOException {
    byte[] data = FileCopyUtils.copyToByteArray(new File("./dummy.prg"));
    byte[] result = new byte[data.length - 0xFF];
    result[0] = 0x00;
    result[1] = 0x50;
    System.arraycopy(data, 0xFF + 2, result, 2, result.length - 2);
    FileCopyUtils.copy(result, new File("./dummy2.prg"));

  }
}

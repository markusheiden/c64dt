package de.heiden.c64dt.assembler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

import de.heiden.c64dt.bytes.HexUtil;
import de.heiden.c64dt.charset.C64Charset;

import static de.heiden.c64dt.common.Requirements.R;

/**
 * Dumps memory.
 */
public class Dumper {
  /**
   * Charset.
   */
  private final C64Charset charset = C64Charset.LOWER;

  /**
   * Dump memory.
   *
   * @param code Code buffer
   * @param output output for reassembled code
   */
  public void dump(ICodeBuffer code, Writer output) throws IOException {
    R.requireThat(code, "code").isNotNull();
    R.requireThat(output, "output").isNotNull();

    try (BufferedWriter out = new NonClosingBufferedWriter(output)) {
      dump(code, out);
    }
    output.flush();
  }

  /**
   * Dump memory.
   *
   * @param code Code buffer
   * @param output output for reassembled code
   */
  private void dump(ICodeBuffer code, BufferedWriter output) throws IOException {
    R.requireThat(code, "code").isNotNull();
    R.requireThat(output, "output").isNotNull();

    StringBuilder chars = new StringBuilder(16);
    while (code.hasMore()) {
      output.append(HexUtil.hexWordPlain(code.getCurrentAddress()));
      output.append("  ");
      for (int i = 0; i < 16; i++) {
        if (code.hasMore()) {
          int b = code.readByte();
          output.append(HexUtil.hexBytePlain(b));
          output.append(" ");

          char c = charset.toChar((byte) b);
          chars.append(c != 0 ? c : '.');
        } else {
          output.append("   ");
        }
      }

      output.append("  ");
      output.append(chars);

      output.newLine();

      chars.setLength(0);
    }
  }
}
package de.heiden.c64dt.assembler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

import static com.github.cowwoc.requirements10.java.DefaultJavaValidators.requireThat;
import static de.heiden.c64dt.bytes.ByteUtil.hi;
import static de.heiden.c64dt.bytes.ByteUtil.lo;
import static de.heiden.c64dt.bytes.HexUtil.hexBytePlain;
import static de.heiden.c64dt.bytes.HexUtil.hexWordPlain;

/**
 * Reassembler.
 */
public class Disassembler {
  /**
   * List basic header and disassemble.
   *
   * @param code Code buffer.
   * @param output Output for reassembled code.
   */
  public void listAndDisassemble(ICodeBuffer code, Writer output) throws IOException {
    listAndDisassemble(code, output, true);
  }

  /**
   * List basic header if program starts at $0801 and disassemble.
   *
   * @param code Code buffer.
   * @param output Output for reassembled code.
   */
  public void disassemble(ICodeBuffer code, Writer output) throws IOException {
    listAndDisassemble(code, output, code.getCurrentAddress() == 0x0801);
  }

  /**
   * List basic header and disassemble.
   *
   * @param code Code buffer.
   * @param output Output for reassembled code.
   * @param list List Basic header?.
   */
  public void listAndDisassemble(ICodeBuffer code, Writer output, boolean list) throws IOException {
    requireThat(code, "code").isNotNull();
    requireThat(output, "output").isNotNull();

    try (BufferedWriter out = new NonClosingBufferedWriter(output)) {
      if (list) {
        new Lister().list(code, out);
      }

      while (code.hasMore()) {
        disassembleOpcode(code, out);
      }
    }
    output.flush();
  }

  /**
   * Disassemble one opcode.
   *
   * @param code Code buffer
   * @param output output for reassembled code
   */
  private void disassembleOpcode(ICodeBuffer code, BufferedWriter output) throws IOException {
    Opcode opcode = code.readOpcode();
    OpcodeMode mode = opcode.getMode();
    int size = mode.getSize();

    int pc = code.getCommandAddress();
    output.append(hexWordPlain(pc));
    output.append("  ");
    output.append(hexBytePlain(opcode.getOpcode()));

    if (opcode.isLegal() && code.has(size)) {
      if (size > 0) {
        int argument = code.read(mode.getSize());

        output.append(size >= 1 ? " " + hexBytePlain(lo(argument)) : "   ");
        output.append(size >= 2 ? " " + hexBytePlain(hi(argument)) : "   ");
        output.append("  ");
        output.append(opcode.getType().toString());
        output.append(" ");
        output.append(mode.toString(pc, argument));
      } else {
        output.append("        ");
        output.append(opcode.getType().toString());
      }
    } else {
      output.append("        ???");
    }
    output.newLine();
  }
}

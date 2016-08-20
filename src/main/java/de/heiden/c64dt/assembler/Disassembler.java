package de.heiden.c64dt.assembler;

import static de.heiden.c64dt.util.ByteUtil.hi;
import static de.heiden.c64dt.util.ByteUtil.lo;
import static de.heiden.c64dt.util.HexUtil.hexBytePlain;
import static de.heiden.c64dt.util.HexUtil.hexWordPlain;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

import org.springframework.util.Assert;

import de.heiden.c64dt.util.NonClosingBufferedWriter;

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
    Assert.notNull(code, "Precondition: code != null");
    Assert.notNull(output, "Precondition: output != null");

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

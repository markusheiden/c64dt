package de.heiden.c64dt.assembler;

import de.heiden.c64dt.charset.C64Charset;
import de.heiden.c64dt.util.NonClosingBufferedWriter;
import org.springframework.util.Assert;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

import static de.heiden.c64dt.util.ByteUtil.hi;
import static de.heiden.c64dt.util.ByteUtil.lo;
import static de.heiden.c64dt.util.HexUtil.hexBytePlain;
import static de.heiden.c64dt.util.HexUtil.hexWordPlain;

/**
 * Reassembler.
 */
public class Disassembler {
  /**
   * Charset.
   */
  private final C64Charset charset = C64Charset.LOWER;

  /**
   * BASIC commands.
   */
  private static final String[] BASIC = {
    "END", "FOR", "NEXT", "DATA", "INPUT#", "INPUT", "DIM", "READ",
    "LET", "GOTO", "RUN", "IF", "RESTORE", "GOSUB", "RETURN", "REM",
    "STOP", "ON", "WAIT", "LOAD", "SAVE", "VERIFY", "DEF", "POKE",
    "PRINT#", "PRINT", "CONT", "LIST", "CLR", "CMD", "SYS", "OPEN",
    "CLOSE", "GET", "NEW", "TAB(", "TO", "FN", "SPC(", "THEN",
    "NOT", "STOP", "+", "-", "*", "/", "^", "AND",
    "OR", "<", "=", ">", "SGN", "INT", "ABS", "USR",
    "FRE", "POS", "SQR", "RND", "LOG", "EXP", "COS", "SIN",
    "TAN", "ATN", "PEEK", "LEN", "STR$", "VAL", "ASC", "CHR$",
    "LEFT$", "RIGHT$", "MID$", "GO"
  };

  /**
   * Reassemble.
   *
   * @param code Code buffer
   * @param output output for reassembled code
   */
  public void disassemble(ICodeBuffer code, Writer output) throws IOException {
    Assert.notNull(code, "Precondition: code != null");
    Assert.notNull(output, "Precondition: output != null");

    try (BufferedWriter out = new NonClosingBufferedWriter(output)) {
      if (code.getCurrentAddress() == 0x0801) {
        list(code, out);
      }

      while (code.hasMore()) {
        disassembleOpcode(code, out);
      }
    }
    output.flush();
  }

  /**
   * List BASIC program.
   *
   * @param code Code buffer
   * @param output output for BASIC listing
   */
  public void list(ICodeBuffer code, Writer output) throws IOException {
    try (BufferedWriter out = new NonClosingBufferedWriter(output)) {
      list(code, out);
    }
    output.flush();
  }

  /**
   * List BASIC program.
   *
   * @param code Code buffer
   * @param output output for BASIC listing
   */
  private void list(ICodeBuffer code, BufferedWriter output) throws IOException {
    while (listLine(code, output)) {
      // continue listing
    }
    output.newLine();
  }

  /**
   * List one BASIC line.
   *
   * @param code Code buffer
   * @param output output for BASIC listing
   * @return Are there more commands?
   */
  private boolean listLine(ICodeBuffer code, BufferedWriter output) throws IOException {
    int pc = code.getCurrentAddress();
    // next address is not used for searching for the next command, because the linking may be broken
    final int nextAddress = code.readWord();
    if (nextAddress == 0) {
      return false;
    }

    output.append(hexWordPlain(pc));
    output.append("  ");
    // line number
    output.append(Integer.toString(code.readWord()));
    output.append(" ");

    boolean escaped = false;
    while (code.hasMore()) {
      int b = code.readByte();
      if (b == 0x00) {
        break;
      }
      escaped = listByte(b, output, escaped);
    }
    output.newLine();

    return true;
  }

  /**
   * List one BASIC byte.
   *
   * @param b BASIC byte
   * @param output output for BASIC listing
   * @param escaped Escape mode active?
   * @return New escape mode
   */
  private boolean listByte(int b, BufferedWriter output, boolean escaped) throws IOException {
    if (b == 0x22) {
      // " toggles escape mode
      escaped = !escaped;
    }

    if (b == 0xFF) {
      // Pi
      output.append("<PI>");
    } else if (b < 0x80 || escaped) {
      // output char unmodified
      output.append(charset.toString((byte) b));
    } else {
      // command
      output.append(BASIC[b - 128]);
    }

    return escaped;
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

  //
  //
  //


  /**
   * Dump memory.
   *
   * @param code Code buffer
   * @param output output for reassembled code
   */
  public void dump(ICodeBuffer code, Writer output) throws IOException {
    Assert.notNull(code, "Precondition: code != null");
    Assert.notNull(output, "Precondition: output != null");

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
    Assert.notNull(code, "Precondition: code != null");
    Assert.notNull(output, "Precondition: output != null");

    C64Charset charset = C64Charset.LOWER;

    StringBuilder chars = new StringBuilder(16);
    while (code.hasMore()) {
      output.append(hexWordPlain(code.getCurrentAddress()));
      output.append("  ");
      for (int i = 0; i < 16; i++) {
        if (code.hasMore()) {
          int b = code.readByte();
          output.append(hexBytePlain(b));
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

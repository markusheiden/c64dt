package de.heiden.c64dt.assembler;

import de.heiden.c64dt.bytes.HexUtil;
import de.heiden.c64dt.charset.C64Charset;
import de.heiden.c64dt.util.NonClosingBufferedWriter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Lists BASIC programs.
 */
public class Lister {
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
   * Charset.
   */
  private final C64Charset charset = C64Charset.LOWER;

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
  void list(ICodeBuffer code, BufferedWriter output) throws IOException {
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
  boolean listLine(ICodeBuffer code, BufferedWriter output) throws IOException {
    int pc = code.getCurrentAddress();
    // next address is not used for searching for the next command, because the linking may be broken
    final int nextAddress = code.readWord();
    if (nextAddress == 0) {
      return false;
    }

    output.append(HexUtil.hexWordPlain(pc));
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
  boolean listByte(int b, BufferedWriter output, boolean escaped) throws IOException {
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
      output.append(BASIC[b - 0x80]);
    }

    return escaped;
  }
}

package de.heiden.c64dt.assembler;

import de.heiden.c64dt.charset.C64Charset;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import static de.heiden.c64dt.util.ByteUtil.*;
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
   * Reassemble program.
   *
   * @param input program with start address
   * @param output output for reassembled code
   */
  public void disassemble(InputStream input, Writer output) throws IOException {
    Assert.notNull(input, "Precondition: input != null");
    Assert.notNull(output, "Precondition: output != null");

    disassemble(FileCopyUtils.copyToByteArray(input), output);
  }

  /**
   * Reassemble program.
   *
   * @param program program with start address
   * @param output output for reassembled code
   */
  public void disassemble(byte[] program, Writer output) throws IOException {
    Assert.notNull(program, "Precondition: program != null");
    Assert.isTrue(program.length >= 2, "Precondition: program.length >= 2");
    Assert.notNull(output, "Precondition: output != null");

    int address = toWord(program, 0);
    byte[] code = new byte[program.length - 2];
    System.arraycopy(program, 2, code, 0, code.length);
    disassemble(address, code, output);
  }

  /**
   * Reassemble.
   *
   * @param startAddress start address of code
   * @param code code
   * @param output output for reassembled code
   */
  public void disassemble(int startAddress, byte[] code, Writer output) throws IOException {
    Assert.isTrue(startAddress >= 0, "Precondition: startAddress >= 0");
    Assert.notNull(code, "Precondition: code != null");
    Assert.notNull(output, "Precondition: output != null");

    CodeBuffer buffer = new CodeBuffer(startAddress, code);

    if (startAddress == 0x0801) {
      list(buffer, output);
    }

    while (buffer.has(1)) {
      disassemble(buffer, output);
    }

    output.flush();
  }

  /**
   * List BASIC program.
   *
   * @param buffer Code buffer
   * @param output output for BASIC listing
   */
  public void list(ICodeBuffer buffer, Writer output) throws IOException {
    while (listLine(buffer, output)) {
      // continue listing
    }
    output.append("\n");
  }

  /**
   * List one BASIC line.
   *
   * @param buffer Code buffer
   * @param output output for BASIC listing
   * @return Are there more commands?
   */
  private boolean listLine(ICodeBuffer buffer, Writer output) throws IOException {
    int pc = buffer.getCurrentAddress();
    // next address is not used for searching for the next command, because the linking may be broken
    final int nextAddress = buffer.readWord();
    if (nextAddress == 0) {
      return false;
    }

    output.append(hexWordPlain(pc));
    output.append(" ");
    // line number
    output.append(Integer.toString(buffer.readWord()));
    output.append(" ");

    boolean escaped = false;
    while (buffer.has(1)) {
      int b = buffer.readByte();
      if (b == 0x00) {
        break;
      }
      escaped = listByte(b, output, escaped);
    }
    output.append("\n");

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
  private boolean listByte(int b, Writer output, boolean escaped) throws IOException {
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
   * @param buffer Code buffer
   * @param output output for reassembled code
   */
  public void disassemble(ICodeBuffer buffer, Writer output) throws IOException {
    Opcode opcode = buffer.readOpcode();
    OpcodeMode mode = opcode.getMode();
    int size = mode.getSize();

    int pc = buffer.getCommandAddress();
    output.append(hexWordPlain(pc));
    output.append(" ");
    output.append(hexBytePlain(opcode.getOpcode()));

    if (opcode.isLegal() && buffer.has(size)) {
      if (size > 0) {
        int argument = buffer.read(mode.getSize());

        output.append(size >= 1 ? " " + hexBytePlain(lo(argument)) : "   ");
        output.append(size >= 2 ? " " + hexBytePlain(hi(argument)) : "   ");
        output.append(" ");
        output.append(opcode.getType().toString());
        output.append(" ");
        output.append(mode.toString(pc, argument));
      } else {
        output.append("       ");
        output.append(opcode.getType().toString());
      }
    } else {
      output.append("       ???");
    }
    output.append("\n");
  }
}

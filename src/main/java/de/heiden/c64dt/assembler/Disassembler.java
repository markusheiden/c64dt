package de.heiden.c64dt.assembler;

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
      // TODO check for basic header
    }

    while (buffer.has(1)) {
      disassemble(buffer, output);
    }

    output.flush();
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

package de.heiden.c64dt.assembler;

import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import static de.heiden.c64dt.util.ByteUtil.hi;
import static de.heiden.c64dt.util.ByteUtil.lo;
import static de.heiden.c64dt.util.ByteUtil.toWord;
import static de.heiden.c64dt.util.HexUtil.format2;
import static de.heiden.c64dt.util.HexUtil.format4;

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

    while(buffer.has(1)) {
      Opcode opcode = buffer.readOpcode();
      OpcodeMode mode = opcode.getMode();
      int size = mode.getSize();

      output.append(format4(buffer.getCommandAddress()));
      output.append(" ");
      output.append(format2(opcode.getOpcode()));

      if (opcode.isLegal() && buffer.has(size)) {
        if (size > 0) {
          int address = mode == OpcodeMode.REL? buffer.readRelative() : buffer.readAbsolute(size);

          output.append(size >= 1? " " + format2(lo(address)) : "   ");
          output.append(size >= 2? " " + format2(hi(address)) : "   ");
          output.append(" ");
          output.append(opcode.getType().toString());
          output.append(" ");
          output.append(mode.toString(address));
        } else {
          output.append("       ");
          output.append(opcode.getType().toString());
        }
      } else {
        output.append("       ???");
      }
      output.append("\n");
    }

    output.flush();
  }
}

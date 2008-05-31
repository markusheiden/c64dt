package de.markusheiden.c64dt.assembler;

import static de.markusheiden.c64dt.util.ByteUtil.toWord;
import static de.markusheiden.c64dt.util.HexUtil.format4;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

/**
 * Reassembler.
 */
public class Reassembler {
  /**
   * Reassemble program.
   *
   * @param input program with start address
   * @param output output for reassembled code
   */
  public void reassemble(InputStream input, Writer output) throws IOException {
    Assert.notNull(input, "Precondition: input != null");
    Assert.notNull(output, "Precondition: output != null");

    reassemble(FileCopyUtils.copyToByteArray(input), output);
  }

  /**
   * Reassemble program.
   *
   * @param program program with start address
   * @param output output for reassembled code
   */
  public void reassemble(byte[] program, Writer output) throws IOException {
    Assert.notNull(program, "Precondition: program != null");
    Assert.isTrue(program.length >= 2, "Precondition: program.length >= 2");
    Assert.notNull(output, "Precondition: output != null");

    int address = toWord(program, 0);
    byte[] code = new byte[program.length - 2];
    System.arraycopy(program, 2, code, 0, code.length);
    reassemble(address, code, output);
  }

  /**
   * Reassemble.
   *
   * @param startAddress start address of code
   * @param code code
   * @param output output for reassembled code
   */
  public void  reassemble(int startAddress, byte[] code, Writer output) throws IOException {
    Assert.isTrue(startAddress >= 0, "Precondition: startAddress >= 0");
    Assert.notNull(code, "Precondition: code != null");
    Assert.notNull(output, "Precondition: output != null");

    CodeBuffer buffer = new CodeBuffer(startAddress, code);

    if (startAddress == 0x0801) {
      // TODO check for basic header
    }

    // Convert every byte to its opcode
    Set<Integer> labels = new HashSet<Integer>();

    scan(buffer, labels);
    analyze(buffer, labels);
    writeOutput(buffer, labels, output);

    output.flush();
  }

  private void scan(CodeBuffer buffer, Set<Integer> labels) {
    while(buffer.has(1)) {
      Opcode opcode = buffer.readOpcode();
      OpcodeMode mode = opcode.getMode();
      int size = mode.getSize();

      ICommand command;
      if (opcode.isLegal() && buffer.has(size)) {
        if (size > 0) {
          int address = mode == OpcodeMode.REL? buffer.readRelative() : buffer.readAbsolute(size);
          command = new OpcodeCommand(opcode, address);
          if (mode.isAddress() && buffer.hasAddress(address)) {
            labels.add(address);
          }
        } else {
          command = new OpcodeCommand(opcode);
        }
      } else {
        command = new UnknownCommand(opcode.getOpcode());
      }
      buffer.setCommand(command);
    }
  }

  private void analyze(CodeBuffer buffer, Set<Integer> labels) {
    buffer.restart();

    ICommand command = new UnknownCommand();
    while (buffer.has(1)) {
      boolean reachable = (command != null && !command.isEnd()) || labels.contains(buffer.getAddress());
      command = buffer.readCommand();
      if (command != null) {
        command.setReachable(reachable);
      }
    }
  }

  private void writeOutput(CodeBuffer buffer, Set<Integer> labels, Writer output) throws IOException {
    buffer.restart();

    while (buffer.has(1)) {
      int pc = buffer.getAddress();
      ICommand command = buffer.readCommand();
      if (labels.contains(pc)) {
        output.append("L");
        output.append(format4(pc));
        output.append(":    ");
      } else if (command == null) {
        output.append("?         ");
      } else if (!command.isReachable()) {
        output.append("U         ");
      } else {
        output.append("          ");
      }

      if (command instanceof OpcodeCommand) {
        Opcode opcode = ((OpcodeCommand) command).getOpcode();
        OpcodeMode mode = opcode.getMode();
        int address = ((OpcodeCommand) command).getArgument();

        output.append(opcode.getType().toString());
        if (mode.getSize() > 0) {
          output.append(" ");
          output.append(labels.contains(address)? mode.toString("L" + format4(address)) :  mode.toString(address));
        }
      } else if (command instanceof DataCommand) {
        output.append("DAT");
      } else {
        output.append("???");
      }
      output.append("\n");
    }

    output.flush();
  }
}

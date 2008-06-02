package de.markusheiden.c64dt.assembler;

import static de.markusheiden.c64dt.util.ByteUtil.toWord;
import static de.markusheiden.c64dt.util.HexUtil.format4;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

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

    tokenize(buffer);
    reachability(buffer);
    combine(buffer);
    write(buffer, output);

    output.flush();
  }

  private void tokenize(CodeBuffer buffer) {
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
            buffer.addReference(opcode.getType().isJump(), address);
          }
        } else {
          command = new OpcodeCommand(opcode);
        }
      } else {
        command = new DataCommand(opcode.getOpcode());
      }
      buffer.setCommand(command);
    }
  }

  private void reachability(CodeBuffer buffer) {
    // transitive unreachability
    do {
      buffer.restart();

      ICommand lastCommand = null;
      while (!buffer.isEnd()) {
        ICommand command = buffer.readCommand();
        if (command != null) {
          if (command.isReachable() && commandIsNotReachable(buffer, lastCommand, command)) {
            command.setReachable(false);
            if (buffer.removeReference()) {
              // restart, because reference caused a wrong label in the already scanned code
              break;
            }
          }

          lastCommand = command;
        } else {
          // TODO log error?
          lastCommand = null;
        }
      }
    } while (!buffer.isEnd());
  }

  private boolean commandIsNotReachable(CodeBuffer buffer, ICommand last, ICommand current) {
    if (!current.isReachable()) {
      // this command is already marked as not reachable
      return true;
    } else if (last == null || last.isEnd() || !last.isReachable()) {
      // if there is no chance, that this command can be reached from the previous command,
      // then check, if there is a code label, which may make this command reachable
      return !buffer.hasCodeLabel();
    } else {
      // this command can be reached
      return false;
    }
  }

  private void combine(CodeBuffer buffer) {
    buffer.restart();

    ICommand lastCommand = null;
    while (buffer.has(1)) {
      ICommand command = buffer.readCommand();
      if (command != null) {
        if (lastCommand != null && lastCommand.combineWith(command)) {
          buffer.removeCommand();
        } else {
          lastCommand = command;
        }
      } else {
        // TODO log error?
        lastCommand = null;
      }
    }
  }

  private void write(CodeBuffer buffer, Writer output) throws IOException {
    buffer.restart();

    while (buffer.has(1)) {
      String label = buffer.getLabel();

      output.append(format4(buffer.getAddress()));
      output.append(" ");

      ICommand command = buffer.readCommand();

      // debug prefixes...
      int prefixes = 0;
      if (command == null) {
        output.append("?");
        prefixes++;
      } else if (!command.isReachable()) {
        output.append("U");
        prefixes++;
      }

      // fill remaining prefix space
      for (; prefixes < 4; prefixes++) {
        output.append(" ");
      }

      if (label != null) {
        output.append(label);
        output.append(":    ");
      } else {
        output.append("          ");
      }

      if (command != null) {
        command.toString(buffer, output);
      } else {
        // TODO log error?
        output.append("???");
      }

      output.append("\n");
    }

    output.flush();
  }
}

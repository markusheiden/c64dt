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

    CodeStream stream = new CodeStream(startAddress, code);

    if (startAddress == 0x0801) {
      // TODO check for basic header
    }

    // Convert every byte to its opcode
    Set<Integer> labels = new HashSet<Integer>();

    ICommand[] commands = scan(stream, labels);
    analyze(stream, labels, commands);
    writeOutput(stream, labels, commands, output);

    output.flush();
  }

  private ICommand[] scan(CodeStream stream, Set<Integer> labels) {
    ICommand[] commands = new ICommand[stream.getSize()];
    while(stream.has(1)) {
      int pc = stream.getAddress();
      Opcode opcode = Opcode.opcode(stream.read());
      OpcodeMode mode = opcode.getMode();
      int size = mode.getSize();

      ICommand command;
      if (opcode.isLegal() && stream.has(size)) {
        if (size > 0) {
          int address = mode == OpcodeMode.REL? stream.readRelative() : stream.readAbsolute(size);
          command = new OpcodeCommand(opcode, address);
          if (mode.isAddress() && stream.hasAddress(address)) {
            labels.add(address);
          }
        } else {
          command = new OpcodeCommand(opcode);
        }
      } else {
        command = new UnknownCommand(opcode.getOpcode());
      }
      commands[pc - stream.getStartAddress()] = command;
    }

    return commands;
  }

  private void analyze(CodeStream stream, Set<Integer> labels, ICommand[] commands) {
    ICommand lastCommand = new UnknownCommand();
    for (int i = 0, pc = stream.getStartAddress(); i < stream.getSize(); i++, pc++) {
      ICommand command = commands[i];
      if (command != null) {
        command.setReachable(!command.isEnd() || labels.contains(pc));
        i += command.getSize() - 1;
        pc += command.getSize() - 1;
      }
    }
  }

  private void writeOutput(CodeStream stream, Set<Integer> labels, ICommand[] commands, Writer output) throws IOException {
    for (int i = 0, pc = stream.getStartAddress(); i < commands.length; i++, pc++) {
      ICommand command = commands[i];
      if (labels.contains(pc)) {
        output.append("L");
        output.append(format4(pc));
        output.append(":    ");
      } else if (command == null) {
        output.append("???       ");
      } else if (!command.isReachable()) {
        output.append("unreach   ");
      } else {
        output.append("          ");
//        output.append(format4(startAddress + i));
//        output.append("      ");
      }

      command = commands[i];
      if (command instanceof OpcodeCommand) {
        Opcode opcode = ((OpcodeCommand) command).getOpcode();
        OpcodeMode mode = opcode.getMode();
        int size = mode.getSize();
        int address = ((OpcodeCommand) command).getArgument();

        output.append(opcode.getType().toString());
        if (size > 0) {
          i += size;
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

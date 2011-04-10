package de.heiden.c64dt.assembler;

import de.heiden.c64dt.assembler.command.AddressCommand;
import de.heiden.c64dt.assembler.command.CommandBuffer;
import de.heiden.c64dt.assembler.command.DataCommand;
import de.heiden.c64dt.assembler.command.ICommand;
import de.heiden.c64dt.assembler.command.OpcodeCommand;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.List;

import static de.heiden.c64dt.util.ByteUtil.toWord;
import static de.heiden.c64dt.util.HexUtil.hexBytePlain;
import static de.heiden.c64dt.util.HexUtil.hexWordPlain;

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
   * @param code program without start address
   * @param output output for reassembled code
   */
  public void  reassemble(int startAddress, InputStream code, Writer output) throws IOException {
    Assert.notNull(code, "Precondition: input != null");
    Assert.notNull(output, "Precondition: output != null");

    reassemble(startAddress, FileCopyUtils.copyToByteArray(code), output);
  }

  /**
   * Reassemble.
   *
   * @param startAddress start address of code
   * @param code program without start address
   * @param output output for reassembled code
   */
  public void  reassemble(int startAddress, byte[] code, Writer output) throws IOException {
    Assert.isTrue(startAddress >= 0, "Precondition: startAddress >= 0");
    Assert.notNull(code, "Precondition: code != null");
    Assert.notNull(output, "Precondition: output != null");


    if (startAddress == 0x0801) {
      // TODO check for basic header
    }

    reassemble(new CommandBuffer(startAddress), code, output);
  }

  /**
   * Reassemble.
   *
   * @param commands command buffer
   * @param code program without start address
   * @param output output for reassembled code
   */
  public void reassemble(CommandBuffer commands, byte[] code, Writer output) throws IOException {
    Assert.notNull(code, "Precondition: code != null");
    Assert.notNull(output, "Precondition: output != null");


    CodeBuffer buffer = new CodeBuffer(commands.getStartAddress(), code);

    boolean change = true;
    for (int count = 0; change && count < 100; count++) {
      commands.clear();
      tokenize(buffer, commands);
      change =
        reachability(commands) ||
        detectCodeType(buffer, commands);
    }
    combine(commands);
    write(commands, new BufferedWriter(output, code.length * 80));
  }

  /**
   * Tokenize code.
   *
   * @param code code buffer
   * @param commands reassembled commands
   */
  private void tokenize(CodeBuffer code, CommandBuffer commands) {
    Assert.notNull(code, "Precondition: code != null");

    code.restart();
    while(code.has(1)) {
      int pc = code.getCurrentAddress();
      CodeType type = commands.getType(pc);
      if (type.isData()) {
        // data
        // TODO read multiple data bytes at once?
        commands.addCommand(new DataCommand(code.readByte()));
      } else if (type == CodeType.ABSOLUTE_ADDRESS) {
        int address = code.read(2);
        commands.addCommand(new AddressCommand(address));
        commands.addCodeReference(pc, address);
      } else {
        // unknown or code -> try to disassemble an opcode
        Opcode opcode = code.readOpcode();
        OpcodeMode mode = opcode.getMode();
        int size = mode.getSize();

        if (code.has(1 + size)) {
          if (opcode.isLegal() || type == CodeType.OPCODE) {
            // TODO log error if illegal opcode and type is OPCODE?
            if (size == 0) {
              // opcode without argument
              commands.addCommand(new OpcodeCommand(opcode));
            } else {
              // opcode with an argument
              int argument = code.read(mode.getSize());
              commands.addCommand(new OpcodeCommand(opcode, argument));
              if (mode.isAddress()) {
                int address = mode.getAddress(pc, argument);
                if (code.hasAddress(address)) {
                  // track references of opcodes
                  commands.addReference(opcode.getType().isJump(), pc, address);
                }
              }
            }
          } else {
            // no valid opcode -> assume data
            commands.addCommand(new DataCommand(opcode.getOpcode()));
          }
        } else {
          // not enough argument bytes for opcode -> assume data
          // TODO log error, when type != UNKNOWN?
          commands.addCommand(new DataCommand(opcode.getOpcode()));
        }
      }
    }
  }

  /**
   * Compute reachability of commands.
   *
   * @param buffer command buffer
   * @return whether a code label has been altered
   */
  private boolean reachability(CommandBuffer buffer) {
    // transitive unreachability
    boolean result = false;

    buffer.restart();

    ICommand lastCommand = null;
    while (buffer.hasNextCommand()) {
      ICommand command =  buffer.nextCommand();
      if (command.isReachable() && commandIsNotReachable(buffer, lastCommand)) {
        command.setReachable(false);
        if (buffer.removeReference()) {
          // restart, because reference caused a wrong label in the already scanned code
          result = true;
        }
      }

      lastCommand = command;
    }

    return result;
  }

  /**
   * Compute if command is reachable
   *
   * @param buffer command buffer
   * @param lastCommand last command
   */
  private boolean commandIsNotReachable(CommandBuffer buffer, ICommand lastCommand) {
    if (lastCommand == null || lastCommand.isEnd() || !lastCommand.isReachable()) {
      // if there is no chance, that this command can be reached from the previous command,
      // then check, if there is a code label, which may make this command reachable
      return !buffer.hasCodeLabel();
    } else {
      // this command can be reached
      return false;
    }
  }

  /**
   * Detect type of code.
   *
   * @param commands command commands
   * @return whether a code label has been altered
   */
  private boolean detectCodeType(CodeBuffer code, CommandBuffer commands) {
    boolean result = false;

    // Mark all code label positions as a start of an opcode
    commands.restart();
    while(commands.hasNextCommand()) {
      ICommand command = commands.nextCommand();
      if (commands.hasCodeLabel()) {
        commands.setType(command.getAddress(), CodeType.OPCODE);
      }
      if (command instanceof OpcodeCommand) {
        int address = command.getAddress();
        for (int pc = address + 1; pc < address + command.getSize(); pc++) {
          if (commands.hasCodeLabel(address)) {
            // TODO set data
            // TODO set opcode
            commands.setType(pc, CodeType.OPCODE);
          }
        }
      }
    }

    return result;
  }

  /**
   * Combine commands, if possible.
   *
   * @param buffer command buffer
   */
  private void combine(CommandBuffer buffer) {
    Assert.notNull(buffer, "Precondition: buffer != null");

    buffer.restart();

    ICommand lastCommand = null;
    while (buffer.hasNextCommand()) {
      ICommand command = buffer.nextCommand();
      if (!buffer.hasLabel() && lastCommand != null && lastCommand.combineWith(command)) {
        // TODO let command buffer handle this functionality?
        buffer.removeCurrentCommand();
      } else {
        lastCommand = command;
      }
    }
  }

  /**
   * Write commands to output writer.
   *
   * @param buffer command buffer
   * @param output writer to write output to
   */
  private void write(CommandBuffer buffer, Writer output) throws IOException {
    Assert.notNull(buffer, "Precondition: buffer != null");

    buffer.restart();

    while (buffer.hasNextCommand()) {
      ICommand command = buffer.nextCommand();

      // debug output: prefixes
      int pc = command.getAddress();
      int prefixes = 0;
      if (command == null) {
        output.append("?");
        prefixes++;
      } else {
        if (!command.isReachable()) {
          output.append("U");
          prefixes++;
        }
        for (int i = 1; i < command.getSize(); i++) {
          if (buffer.hasCodeLabel(pc + i)) {
            output.append("C");
            prefixes++;
          }
          if (buffer.hasDataLabel(pc + i)) {
            output.append("D");
            prefixes++;
          }
        }
      }

      // fill remaining prefix space
      for (; prefixes < 5; prefixes++) {
        output.append(" ");
      }

      // debug output: byte representation of command
      output.append(" | ");
      output.append(hexWordPlain(pc));
      List<Integer> data = command.toBytes();
      for (int i = 0; i < data.size() && i < 3; i++) {
        output.append(" ");
        output.append(hexBytePlain(data.get(i)));
      }
      for (int i = data.size(); i < 3; i++) {
        output.append("   ");
      }
      output.append(data.size() > 3? "..." : "   ");
      output.append(" | ");

      // reassembler output
      ILabel label = buffer.getLabel();
      if (label != null) {
        // TODO check length of label
        output.append(label.toString());
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

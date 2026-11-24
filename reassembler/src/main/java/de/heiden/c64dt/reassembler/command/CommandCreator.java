package de.heiden.c64dt.reassembler.command;

import de.heiden.c64dt.assembler.CodeBuffer;
import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.assembler.OpcodeType;

import static com.github.cowwoc.requirements10.java.DefaultJavaValidators.requireThat;

/**
 * Creates the commands.
 */
public class CommandCreator {
  private final CommandBuffer commandBuffer;

  /**
   * Index of the last added command.
   */
  private int index;

  /**
   * Constructor.
   *
   * @param commandBuffer Command buffer.
   */
  public CommandCreator(CommandBuffer commandBuffer) {
    this.commandBuffer = commandBuffer;
  }

  /**
   * Update commands.
   */
  public void createCommands() {
    create();
    combine();
    unreachability();
  }

  /**
   * Tokenize command buffer.
   */
  private void create() {
    var code = new CodeBuffer(commandBuffer.getStartAddress(), commandBuffer.getCode());

    commandBuffer.clear();
    while (code.hasMore()) {
      var codeIndex = code.getCurrentIndex();

      requireThat(codeIndex, "codeIndex").isEqualTo(index, "index");
      var type = commandBuffer.getType(index);

      if (type == CodeType.BIT) {
        // BIT opcode used just to skip the next opcode.
        var opcode = code.readOpcode();
        var modeSize = opcode.getMode().getSize();

        if (opcode.getType() == OpcodeType.BIT && modeSize > 0 && code.has(modeSize)) {
          var argumentIndex = code.getCurrentIndex();
          // Reset code buffer to the argument, because this should be the skipped opcode.
          code.setCurrentIndex(argumentIndex);
          addCommand(new BitCommand(opcode, code.read(modeSize)));
        } else {
          // No BIT opcode -> assume data.
          code.setCurrentIndex(codeIndex);
          addCommand(new DataCommand(code.readByte()));
        }
      } else if (type == CodeType.ADDRESS) {
        // Absolute address as data.
        int address;
        if (code.has(2) && commandBuffer.hasAddress(address = code.read(2))) {
          commandBuffer.addCodeReference(index, address);
          addCommand(new AddressCommand(address));
        } else {
          code.setCurrentIndex(codeIndex);
          addCommand(new DataCommand(code.readByte(), code.readByte()));
        }
      } else if (type == CodeType.DATA) {
        // Plain data.
        addCommand(new DataCommand(code.readByte()));
      } else {
        // Unknown or code -> try to disassemble an opcode.
        var opcode = code.readOpcode();
        var mode = opcode.getMode();
        var modeSize = mode.getSize();

        if (code.has(modeSize) && (opcode.isLegal() || type == CodeType.OPCODE)) {
          // TODO mh: Log error if illegal opcode and type is OPCODE?
          var argument = code.read(modeSize);
          if (mode.isAddress()) {
            var pc = commandBuffer.addressForIndex(index);
            var address = mode.getAddress(pc, argument);
            // Track references of opcodes.
            commandBuffer.addReference(opcode.getType().isJump(), index, address);
          }
          addCommand(new OpcodeCommand(opcode, argument));
        } else {
          // Not enough argument bytes for opcode or illegal opcode -> assume data.
          code.setCurrentIndex(codeIndex);
          addCommand(new DataCommand(code.readByte()));
        }
      }
    }
  }

  /**
   * Add a command at the end of the buffer.
   *
   * @param command Command.
   */
  public void addCommand(ICommand command) {
    requireThat(command.hasAddress(), "command.hasAddress()").isFalse();

    commandBuffer.setCommand(index, command);
    index += command.getSize();
  }

  /**
   * Combine commands, if possible.
   */
  private void combine() {
    ICommand lastCommand = null;
    for (var iter = commandBuffer.iterator(); iter.hasNext(); ) {
      var command = iter.next();
      if (!iter.hasLabel() && lastCommand != null && lastCommand.combineWith(command)) {
        // TODO Let command buffer handle this functionality?
        iter.remove();
      } else {
        lastCommand = command;
      }
    }
  }

  /**
   * Detects reachability of code.
   * Computes transitive unreachability of commands.
   */
  private void unreachability() {
    // Initially mark all opcodes as reachable.
    for (var command : commandBuffer) {
      command.setReachable(command instanceof OpcodeCommand || command instanceof BitCommand);
    }

    // Trace backward from unreachable command to the previous.
    ICommand lastCommand = new DummyCommand();
    for (var iter = commandBuffer.iterator().reverse(); iter.hasPrevious(); ) {
      var command = iter.previous();
      /*
       * A code command is not reachable, if it leads to unreachable code.
       * Exception is JSR, because its argument may follow directly after the instruction.
       *
       * TODO mh: Check JMP/JSR/Bxx targets for reachability?
       */
      if (!lastCommand.isReachable() &&
        command.isReachable() && !command.isEnd() && !isJsr(command) && !iter.getType().isCode()) {
        command.setReachable(false);
        iter.removeReference();
      }

      lastCommand = command;
    }
  }

  /**
   * Check if command is a JSR.
   *
   * @param command Command.
   */
  private boolean isJsr(ICommand command) {
    return command instanceof OpcodeCommand opcodeCommand && opcodeCommand.getOpcode().getType() == OpcodeType.JSR;
  }
}

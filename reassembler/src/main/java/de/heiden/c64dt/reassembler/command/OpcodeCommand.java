package de.heiden.c64dt.reassembler.command;

import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.assembler.Opcode;
import de.heiden.c64dt.assembler.OpcodeMode;
import de.heiden.c64dt.bytes.ByteUtil;
import de.heiden.c64dt.reassembler.label.ILabel;

import java.util.ArrayList;
import java.util.List;

import static org.bitbucket.cowwoc.requirements.core.Requirements.requireThat;

/**
 * Command for an opcode.
 */
public class OpcodeCommand extends AbstractCommand {
  private final Opcode opcode;
  private final int argument;
  private final int size;
  private final boolean end;

  /**
   * Constructor for opcodes with no argument.
   *
   * @param opcode opcode
   */
  public OpcodeCommand(Opcode opcode) {
    this(opcode, -1);
  }

  /**
   * Constructor for opcodes with an argument.
   * All opcodes are reachable for first.
   *
   * @param opcode opcode
   * @param argument argument
   */
  public OpcodeCommand(Opcode opcode, int argument) {
    super(CodeType.OPCODE);

    requireThat("opcode", opcode).isNotNull();

    this.opcode = opcode;
    this.argument = argument;
    this.size = 1 + opcode.getMode().getSize();
    this.end = opcode.getType().isEnd();

  }

  /**
   * Opcode.
   */
  public Opcode getOpcode() {
    return opcode;
  }

  /**
   * Argument for opcode, if any.
   */
  public int getArgument() {
    requireThat("getSize()", getSize()).isGreaterThan(1);

    return argument;
  }

  /**
   * Is the argument an (absolute) address?
   */
  public boolean isArgumentAddress() {
    return opcode.getMode().isAddress();
  }

  /**
   * Get the opcode argument as absolute address.
   */
  public int getArgumentAddress() {
    requireThat("isArgumentAddress()", isArgumentAddress()).isTrue();

    return opcode.getMode().getAddress(getAddress(), getArgument());
  }

  /**
   * Size of opcode including the argument.
   */
  @Override
  public final int getSize() {
    return size;
  }

  /**
   * Does the control flow may not reach the opcode directly after this opcode?
   */
  @Override
  public final boolean isEnd() {
    return end;
  }

  @Override
  public String toString(CommandBuffer buffer) {
    requireThat("buffer", buffer).isNotNull();

    StringBuilder result = new StringBuilder();
    result.append(opcode.getType().toString());
    OpcodeMode mode = opcode.getMode();
    if (mode.getSize() > 0) {
      result.append(" ");
      ILabel label = mode.isAddress() ? buffer.getLabel(mode.getAddress(getAddress(), argument)) : null;
      if (label != null) {
        result.append(mode.toString(label.toString(mode.getAddress(getAddress(), argument))));
      } else {
        result.append(mode.toString(getAddress(), argument));
      }
    }

    return result.toString();
  }

  @Override
  public List<Integer> toBytes() {
    List<Integer> result = new ArrayList<>(getSize());
    result.add(opcode.getOpcode());
    if (opcode.getMode().getSize() == 1) {
      result.add(argument);
    } else if (opcode.getMode().getSize() == 2) {
      result.add(ByteUtil.lo(argument));
      result.add(ByteUtil.hi(argument));
    }


    requireThat("result", result).isNotNull();
    return result;
  }
}

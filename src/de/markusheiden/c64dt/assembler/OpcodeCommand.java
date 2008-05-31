package de.markusheiden.c64dt.assembler;

import org.springframework.util.Assert;

/**
 * Command for an opcode.
 */
public class OpcodeCommand extends AbstractCommand {
  private Opcode opcode;
  private int argument;

  public OpcodeCommand(Opcode opcode) {
    this(opcode, -1);
  }

  public OpcodeCommand(Opcode opcode, int argument) {
    Assert.notNull(opcode, "Precondition: opcode != null");

    this.opcode = opcode;
    this.argument = argument;
  }

  public Opcode getOpcode() {
    return opcode;
  }

  public int getArgument() {
    return argument;
  }

  public int getSize() {
    return 1 + opcode.getMode().getSize();
  }

  public boolean isEnd() {
    return opcode.isEnd();
  }
}

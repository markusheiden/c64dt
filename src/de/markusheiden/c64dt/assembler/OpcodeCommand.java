package de.markusheiden.c64dt.assembler;

import org.springframework.util.Assert;

import java.io.IOException;
import java.io.Writer;

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
    return opcode.getType().isEnd();
  }

  public boolean combineWith(ICommand command) {
    // no combine support needed yet
    return false;
  }

  public void toString(CodeBuffer buffer, Writer output) throws IOException {
    Assert.notNull(buffer, "Precondition: buffer != null");
    Assert.notNull(output, "Precondition: output != null");

    output.append(opcode.getType().toString());
    if (opcode.getMode().getSize() > 0) {
      output.append(" ");
      String label = buffer.getLabel(argument);
      output.append(label != null? opcode.getMode().toString(label) :  opcode.getMode().toString(argument));
    }
  }
}

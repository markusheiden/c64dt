package de.markusheiden.c64dt.assembler.command;

import de.markusheiden.c64dt.assembler.ILabel;
import de.markusheiden.c64dt.assembler.Opcode;
import de.markusheiden.c64dt.util.ByteUtil;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

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
   *
   * @param opcode opcode
   * @param argument argument
   */
  public OpcodeCommand(Opcode opcode, int argument) {
    super(true);

    Assert.notNull(opcode, "Precondition: opcode != null");

    this.opcode = opcode;
    this.argument = argument;
    this.size = 1 + opcode.getMode().getSize();
    this.end =  opcode.getType().isEnd();

  }

  public Opcode getOpcode() {
    return opcode;
  }

  public int getArgument() {
    Assert.isTrue(getSize() > 1, "Precondition: getSize() > 1");

    return argument;
  }

  public final int getSize() {
    return size;
  }

  public final boolean isEnd() {
    return end;
  }

  public boolean combineWith(ICommand command) {
    // no combine support needed yet
    return false;
  }

  public void toString(CommandBuffer buffer, Writer output) throws IOException {
    Assert.notNull(buffer, "Precondition: buffer != null");
    Assert.notNull(output, "Precondition: output != null");

    output.append(opcode.getType().toString());
    if (opcode.getMode().getSize() > 0) {
      output.append(" ");
      ILabel label = buffer.getLabel(argument);
      output.append(label != null? opcode.getMode().toString(label.toString()) :  opcode.getMode().toString(argument));
    }
  }

  public List<Integer> toBytes() {
    List<Integer> result = new ArrayList(getSize());
    result.add(opcode.getOpcode());
    if (opcode.getMode().getSize() == 1) {
      result.add(argument);
    } else if (opcode.getMode().getSize() == 2) {
      result.add(ByteUtil.lo(argument));
      result.add(ByteUtil.hi(argument));
    }


    Assert.notNull(result, "Postcondition: result != null");
    return result;
  }
}

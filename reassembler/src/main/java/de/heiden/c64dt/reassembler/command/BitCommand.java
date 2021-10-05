package de.heiden.c64dt.reassembler.command;

import java.util.Collections;
import java.util.List;

import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.assembler.Opcode;

import static de.heiden.c64dt.bytes.HexUtil.hexByte;
import static de.heiden.c64dt.common.Requirements.R;

/**
 * Command for using bit to skip the next opcode.
 */
public class BitCommand extends AbstractCommand {
  private final Opcode opcode;
  private final int argument;

  /**
   * Constructor.
   *
   * @param opcode Opcode
   * @param argument argument for opcode
   */
  public BitCommand(Opcode opcode, int argument) {
    super(CodeType.BIT);

    this.opcode = opcode;
    this.argument = argument;
  }

  @Override
  public final int getSize() {
    return 1;
  }

  @Override
  public final boolean isEnd() {
    return false;
  }

  @Override
  public String toString(CommandBuffer buffer) {
    R.requireThat(buffer, "buffer").isNotNull();

    return "!BYTE " + hexByte(opcode.getOpcode()) + "; " + opcode.toString(getAddress(), argument);
  }

  @Override
  public List<Integer> toBytes() {
    return Collections.singletonList(opcode.getOpcode());
  }
}

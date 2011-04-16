package de.heiden.c64dt.assembler.command;

import de.heiden.c64dt.assembler.Opcode;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.List;

import static de.heiden.c64dt.util.HexUtil.hexByte;

/**
 * Command for using bit to skip the next opcode.
 */
public class BitCommand extends AbstractCommand
{
  private final Opcode opcode;
  private final int argument;

  /**
   * Constructor.
   *
   * @param opcode Opcode
   * @param argument argument for opcode
   */
  public BitCommand(Opcode opcode, int argument)
  {
    super(true);

    this.opcode = opcode;
    this.argument = argument;
  }

  @Override
  public final int getSize()
  {
    return 1;
  }

  @Override
  public final boolean isEnd()
  {
    return false;
  }

  @Override
  public boolean combineWith(ICommand command)
  {
    return false;
  }

  @Override
  public String toString(CommandBuffer buffer) throws IOException
  {
    Assert.notNull(buffer, "Precondition: buffer != null");

    return "!BYTE " + hexByte(opcode.getOpcode()) + "; " + opcode.toString(getAddress(), argument);
  }

  @Override
  public List<Integer> toBytes()
  {
    return Collections.singletonList(opcode.getOpcode());
  }
}

package de.heiden.c64dt.assembler.command;

import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.assembler.label.ILabel;
import de.heiden.c64dt.util.ByteUtil;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static de.heiden.c64dt.util.HexUtil.hexWord;

/**
 * Command for an absolute address referencing code.
 */
public class AddressCommand extends AbstractCommand
{
  /**
   * Referenced absolute code address.
   */
  private int address;

  /**
   * Command for absolute address.
   *
   * @param address address
   */
  public AddressCommand(int address)
  {
    super(CodeType.ABSOLUTE_ADDRESS, false);

    this.address = address;
  }

  @Override
  public final int getSize()
  {
    return 2;
  }

  @Override
  public final boolean isEnd()
  {
    return true;
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

    ILabel label = buffer.getLabel(address);
    Assert.isTrue(label.getAddress() == address, "Check: label.getAddress() == address");
    return "!WORD " + (label != null ? label.toString() : hexWord(address));
  }

  @Override
  public List<Integer> toBytes()
  {
    return Arrays.asList(ByteUtil.lo(address), ByteUtil.hi(address));
  }
}

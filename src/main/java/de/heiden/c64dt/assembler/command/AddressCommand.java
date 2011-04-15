package de.heiden.c64dt.assembler.command;

import de.heiden.c64dt.assembler.ILabel;
import de.heiden.c64dt.util.ByteUtil;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

import static de.heiden.c64dt.util.HexUtil.hexWord;

/**
 * Command for data.
 */
public class AddressCommand extends AbstractCommand
{
  private int address;

  /**
   * Command for absolute address.
   *
   * @param address address
   */
  public AddressCommand(int address)
  {
    super(false);

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
  public void toString(CommandBuffer buffer, Writer output) throws IOException
  {
    Assert.notNull(buffer, "Precondition: buffer != null");
    Assert.notNull(output, "Precondition: output != null");

    ILabel label = buffer.getLabel(address);
    output.append("!WORD ").append(label != null ? label.toString() : hexWord(address));
  }

  @Override
  public List<Integer> toBytes()
  {
    return Arrays.asList(ByteUtil.lo(address), ByteUtil.hi(address));
  }
}

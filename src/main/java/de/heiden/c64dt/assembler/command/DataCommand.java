package de.heiden.c64dt.assembler.command;

import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.charset.C64Charset;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static de.heiden.c64dt.util.HexUtil.hex;
import static de.heiden.c64dt.util.HexUtil.hexByte;

/**
 * Command for data.
 */
public class DataCommand extends AbstractCommand
{
  /**
   * How many bytes a data line (!BYTE) should hold at max.
   */
  private static final int MAX_BYTES = 8;

  /**
   * Data bytes.
   */
  private List<Integer> data;

  /**
   * Constructor.
   *
   * @param data byte this command represents
   */
  public DataCommand(int data)
  {
    super(CodeType.DATA, false);

    this.data = new ArrayList<Integer>(MAX_BYTES);
    this.data.add(data);
  }

  @Override
  public final int getSize()
  {
    return data.size();
  }

  @Override
  public final boolean isEnd()
  {
    return true;
  }

  @Override
  public boolean combineWith(ICommand command)
  {
    // Only other data commands may be merged
    if (!(command instanceof DataCommand))
    {
      return false;
    }

    DataCommand dataCommand = (DataCommand) command;

    // Only merge more than MAX_BYTES if result is a !FILL
    if (data.size() >= MAX_BYTES && (!isSameByte() || !dataCommand.isSameByte() || !data.get(0).equals(dataCommand.data.get(0))))
    {
      return false;
    }

    data.addAll(dataCommand.data);
    return true;
  }

  @Override
  public String toString(CommandBuffer buffer) throws IOException
  {
    Assert.notNull(buffer, "Precondition: buffer != null");

    StringBuilder output = new StringBuilder(16 + data.size() * 8);
    if (data.size() > 8 && isSameByte())
    {
      // special case: the data consists of the same byte over and over again

      output.append("!FILL ");
      output.append(hex(data.size()));
      output.append(", ");
      output.append(hexByte(data.get(0)));

    }
    else
    {
      // default case: different data bytes

      output.append("!BYTE ");
      output.append(hexByte(data.get(0)));
      for (int i = 1; i < data.size(); i++)
      {
        output.append(", ");
        output.append(hexByte(data.get(i)));
      }
      output.append("; '");
      byte[] string = new byte[data.size()];
      for (int i = 0; i < data.size(); i++)
      {
        string[i] = data.get(i).byteValue();
      }
      output.append(C64Charset.LOWER.toString(string));
      output.append("'");
    }

    return output.toString();
  }

  /**
   * Detect if the data consists of the same byte repeated multiple times.
   */
  private boolean isSameByte()
  {
    Integer content = data.get(0);
    for (Integer dataByte : data)
    {
      if (!content.equals(dataByte))
      {
        return false;
      }
    }

    return true;
  }

  @Override
  public List<Integer> toBytes()
  {
    return Collections.unmodifiableList(data);
  }
}

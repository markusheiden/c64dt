package de.heiden.c64dt.assembler.command;

import de.heiden.c64dt.charset.C64Charset;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static de.heiden.c64dt.util.HexUtil.hexByte;

/**
 * Command for data.
 */
public class DataCommand extends AbstractCommand
{
  private List<Integer> data;

  public DataCommand(int data)
  {
    super(false);

    this.data = new ArrayList<Integer>(8);
    this.data.add(data);
  }

  public final int getSize()
  {
    return data.size();
  }

  public final boolean isEnd()
  {
    return true;
  }

  public boolean combineWith(ICommand command)
  {
    if (!(command instanceof DataCommand))
    {
      return false;
    }

    data.addAll(((DataCommand) command).data);
    return true;
  }

  public void toString(CommandBuffer buffer, Writer output) throws IOException
  {
    Assert.notNull(buffer, "Precondition: buffer != null");
    Assert.notNull(output, "Precondition: output != null");

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

  public List<Integer> toBytes()
  {
    return Collections.unmodifiableList(data);
  }
}

package de.heiden.c64dt.assembler.command;

import org.springframework.util.Assert;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static de.heiden.c64dt.util.HexUtil.format2;

/**
 * Command for data.
 */
public class DataCommand extends AbstractCommand {
  private List<Integer> data;

  public DataCommand(int data) {
    super(false);

    this.data = new ArrayList<Integer>(8);
    this.data.add(data);

    setReachable(false);
  }

  public final int getSize() {
    return data.size();
  }

  public final boolean isEnd() {
    return true;
  }

  public boolean combineWith(ICommand command) {
    if (!(command instanceof DataCommand)) {
      return false;
    }

    data.addAll(((DataCommand) command).data);
    return true;
  }

  public void toString(CommandBuffer buffer, Writer output) throws IOException {
    Assert.notNull(buffer, "Precondition: buffer != null");
    Assert.notNull(output, "Precondition: output != null");

    output.append(".DB $");
    output.append(format2(data.get(0)));
    for (int i = 1; i < data.size(); i++) {
      output.append(", $");
      output.append(format2(data.get(i)));
    }
  }

  public List<Integer> toBytes() {
    return Collections.unmodifiableList(data);
  }
}

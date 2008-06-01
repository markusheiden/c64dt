package de.markusheiden.c64dt.assembler;

import static de.markusheiden.c64dt.util.HexUtil.format2;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.ArrayList;

/**
 * Command for data.
 */
public class DataCommand extends AbstractCommand {
  private List<Integer> data;

  public DataCommand(int data) {
    this.data = new ArrayList(8);
    this.data.add(data);
  }

  public int getSize() {
    return data.size();
  }

  public boolean isEnd() {
    return true;
  }

  public boolean combineWith(ICommand command) {
    if (!(command instanceof DataCommand)) {
      return false;
    }

    data.addAll(((DataCommand) command).data);
    return true;
  }

  public void toString(CodeBuffer buffer, Writer output) throws IOException {
    Assert.notNull(buffer, "Precondition: buffer != null");
    Assert.notNull(output, "Precondition: output != null");

    output.append(".DB $");
    output.append(format2(data.get(0)));
    for (int i = 1; i < data.size(); i++) {
      output.append(", $");
      output.append(format2(data.get(i)));
    }
  }
}

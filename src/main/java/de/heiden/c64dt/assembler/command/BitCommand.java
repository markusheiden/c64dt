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
 * Command for using bit to skip the next opcode.
 */
public class BitCommand extends AbstractCommand {
  private int opcode;

  public BitCommand(int opcode) {
    super(true);

    this.opcode = opcode;
  }

  public final int getSize() {
    return 1;
  }

  public final boolean isEnd() {
    return false;
  }

  public boolean combineWith(ICommand command) {
    return false;
  }

  public void toString(CommandBuffer buffer, Writer output) throws IOException {
    Assert.notNull(buffer, "Precondition: buffer != null");
    Assert.notNull(output, "Precondition: output != null");

    output.append("!BYTE ");
    output.append(hexByte(opcode));
    output.append("; BIT");
  }

  public List<Integer> toBytes() {
    return Collections.singletonList(opcode);
  }
}

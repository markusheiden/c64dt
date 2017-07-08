package de.heiden.c64dt.assembler.command;

import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.assembler.label.ILabel;
import de.heiden.c64dt.bytes.ByteUtil;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;

import static de.heiden.c64dt.bytes.HexUtil.hexWord;

/**
 * Command for an absolute address referencing code.
 */
public class AddressCommand extends AbstractCommand {
  /**
   * Referenced absolute code address.
   */
  private final int address;

  /**
   * Command for absolute address.
   *
   * @param address address
   */
  public AddressCommand(int address) {
    super(CodeType.ADDRESS);

    this.address = address;
  }

  @Override
  public final int getSize() {
    return 2;
  }

  @Override
  public final boolean isEnd() {
    return true;
  }

  @Override
  public String toString(CommandBuffer buffer) {
    Assert.notNull(buffer, "Precondition: buffer != null");

    ILabel label = buffer.getLabel(address);
    Assert.isTrue(label == null || label.getAddress() == address, "Check: label.getAddress() == address");
    return "!WORD " + (label != null ? label.toString(address) : hexWord(address));
  }

  @Override
  public List<Integer> toBytes() {
    return Arrays.asList(ByteUtil.lo(address), ByteUtil.hi(address));
  }
}

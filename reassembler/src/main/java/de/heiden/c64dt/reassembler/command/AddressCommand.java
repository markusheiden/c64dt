package de.heiden.c64dt.reassembler.command;

import java.util.Arrays;
import java.util.List;

import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.bytes.ByteUtil;
import de.heiden.c64dt.reassembler.label.ILabel;

import static de.heiden.c64dt.assembler.Requirements.R;
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
    R.requireThat(buffer, "buffer").isNotNull();

    ILabel label = buffer.getLabel(address);
    if (label == null) {
      return "!WORD " + hexWord(address);
    }

    R.requireThat(label.getAddress(), "label.getAddress()").isEqualTo(address, "address");
    return "!WORD " + label.toString(address);
  }

  @Override
  public List<Integer> toBytes() {
    return Arrays.asList(ByteUtil.lo(address), ByteUtil.hi(address));
  }
}

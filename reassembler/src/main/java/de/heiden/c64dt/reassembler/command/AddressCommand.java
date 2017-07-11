package de.heiden.c64dt.reassembler.command;

import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.bytes.ByteUtil;
import de.heiden.c64dt.reassembler.label.ILabel;

import java.util.Arrays;
import java.util.List;

import static de.heiden.c64dt.bytes.HexUtil.hexWord;
import static org.bitbucket.cowwoc.requirements.core.Requirements.requireThat;

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
    requireThat(buffer, "buffer").isNotNull();

    ILabel label = buffer.getLabel(address);
    if (label == null) {
      return "!WORD " + hexWord(address);
    }

    requireThat(label.getAddress(), "label.getAddress()").isEqualTo(address);
    return "!WORD " + label.toString(address);
  }

  @Override
  public List<Integer> toBytes() {
    return Arrays.asList(ByteUtil.lo(address), ByteUtil.hi(address));
  }
}

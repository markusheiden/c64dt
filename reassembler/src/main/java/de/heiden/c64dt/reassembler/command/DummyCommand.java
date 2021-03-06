package de.heiden.c64dt.reassembler.command;

import de.heiden.c64dt.assembler.CodeType;

import java.util.List;

/**
 * Dummy command as replacement for null in the reassembler.
 */
public class DummyCommand extends AbstractCommand {
  public static final ICommand DUMMY_COMMAND = new DummyCommand();

  /**
   * Constructor.
   */
  public DummyCommand() {
    this(0);
  }

  /**
   * Constructor.
   *
   * @param address address
   */
  public DummyCommand(int address) {
    super(CodeType.UNKNOWN);
    setAddress(address);
  }

  @Override
  public final int getSize() {
    return 0;
  }

  @Override
  public final boolean isEnd() {
    return true;
  }

  @Override
  public String toString(CommandBuffer buffer) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Integer> toBytes() {
    throw new UnsupportedOperationException();
  }
}

package de.heiden.c64dt.assembler.command;

import de.heiden.c64dt.assembler.ILabel;
import de.heiden.c64dt.assembler.Opcode;
import de.heiden.c64dt.util.ByteUtil;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
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
    super(false);
    setAddress(0, address);
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
  public boolean combineWith(ICommand command) {
    return false;
  }

  @Override
  public void toString(CommandBuffer buffer, Writer output) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Integer> toBytes() {
    throw new UnsupportedOperationException();
  }
}

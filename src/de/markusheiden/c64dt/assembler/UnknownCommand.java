package de.markusheiden.c64dt.assembler;

/**
 * Unknown opcode command.
 */
public class UnknownCommand extends AbstractCommand {
  private int data;

  /**
   * Constructor.
   */
  public UnknownCommand() {
    this(0);
  }

  /**
   * Constructor.
   *
   * @param data data with unknown meaning
   */
  public UnknownCommand(int data) {
    this.data = data;
  }

  /**
   * Data with unknown meaning.
   */
  public int getData() {
    return data;
  }

  public int getSize() {
    return 1;
  }

  public boolean isEnd() {
    return true;
  }
}

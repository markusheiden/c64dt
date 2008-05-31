package de.markusheiden.c64dt.assembler;

/**
 * Command for data.
 */
public class DataCommand extends AbstractCommand {
  private int data;

  public DataCommand(int data) {
    this.data = data;
  }

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

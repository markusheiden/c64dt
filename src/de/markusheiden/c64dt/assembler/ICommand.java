package de.markusheiden.c64dt.assembler;

/**
 * Command.
 */
public interface ICommand {
  public int getSize();

  public boolean isEnd();

  public boolean isReachable();

  public void setReachable(boolean reachable);
}

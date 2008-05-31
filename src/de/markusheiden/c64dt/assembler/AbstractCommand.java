package de.markusheiden.c64dt.assembler;

/**
 * Base implementation for commands.
 */
public abstract class AbstractCommand implements ICommand {
  private boolean reachable = true;

  public boolean isReachable() {
    return reachable;
  }

  public void setReachable(boolean reachable) {
    this.reachable = reachable;
  }
}

package de.heiden.c64dt.assembler.command;

import org.springframework.util.Assert;

/**
 * Base implementation for commands.
 */
public abstract class AbstractCommand implements ICommand {
  private int address;
  private boolean reachable;

  /**
   * Constructor.
   *
   * @param reachable default value for reachbility
   */
  protected AbstractCommand(boolean reachable) {
    this.address = -1;
    this.reachable = reachable;
  }

  public boolean hasAddress() {
    return address >= 0;
  }

  public int getAddress() {
    Assert.isTrue(hasAddress(), "Precondition: hasAddress()");
    return address;
  }

  public void setAddress(int address) {
    Assert.isTrue(!hasAddress(), "Precondition: !hasAddress()");

    this.address = address;
  }

  public int getNextAddress() {
    Assert.isTrue(hasAddress(), "Precondition: hasAddress()");
    return getAddress() + getSize();
  }

  public final boolean isReachable() {
    return reachable;
  }

  public final void setReachable(boolean reachable) {
    this.reachable = reachable;
  }
}

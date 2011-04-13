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
   * @param reachable default value for reachability
   */
  protected AbstractCommand(boolean reachable) {
    this.address = -1;
    this.reachable = reachable;
  }

  @Override
  public boolean hasAddress() {
    return address >= 0;
  }

  @Override
  public int getAddress() {
    Assert.isTrue(hasAddress(), "Precondition: hasAddress()");
    return address;
  }

  @Override
  public void setAddress(int address) {
    Assert.isTrue(!hasAddress(), "Precondition: !hasAddress()");

    this.address = address;
  }

  @Override
  public int getNextAddress() {
    Assert.isTrue(hasAddress(), "Precondition: hasAddress()");
    return getAddress() + getSize();
  }

  @Override
  public final boolean isReachable() {
    return reachable;
  }

  @Override
  public final void setReachable(boolean reachable) {
    this.reachable = reachable;
  }
}

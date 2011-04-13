package de.heiden.c64dt.assembler.command;

import org.springframework.util.Assert;

/**
 * Base implementation for commands.
 */
public abstract class AbstractCommand implements ICommand {
  private int index;
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
  public int getIndex() {
    Assert.isTrue(hasAddress(), "Precondition: hasAddress()");
    return index;
  }

  @Override
  public int getAddress() {
    Assert.isTrue(hasAddress(), "Precondition: hasAddress()");
    return address;
  }

  @Override
  public void setAddress(int index, int address) {
    Assert.isTrue(index >= 0, "Precondition: !hasAddress()");
    Assert.isTrue(!hasAddress(), "Precondition: !hasAddress()");

    this.index = index;
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

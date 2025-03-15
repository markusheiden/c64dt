package de.heiden.c64dt.reassembler.command;

import de.heiden.c64dt.assembler.CodeType;

import static com.github.cowwoc.requirements10.java.DefaultJavaValidators.requireThat;

/**
 * Base implementation for commands.
 */
public abstract class AbstractCommand implements ICommand {
  private final CodeType type;
  private boolean reachable = false;
  private int address = -1;

  /**
   * Constructor.
   *
   * @param type the code type this command handles
   */
  protected AbstractCommand(CodeType type) {
    this.type = type;
  }

  @Override
  public CodeType getType() {
    return type;
  }

  @Override
  public boolean hasAddress() {
    return address >= 0;
  }

  @Override
  public int getAddress() {
    requireThat(hasAddress(), "hasAddress()").isTrue();
    return address;
  }

  @Override
  public void setAddress(int address) {
    requireThat(hasAddress(), "hasAddress()").isFalse();

    this.address = address;
  }

  @Override
  public final boolean isReachable() {
    return reachable;
  }

  @Override
  public final void setReachable(boolean reachable) {
    this.reachable = reachable;
  }

  @Override
  public boolean combineWith(ICommand command) {
    // Default implementation: No combine support.
    return false;
  }
}

package de.heiden.c64dt.assembler.command;

import de.heiden.c64dt.assembler.CodeType;
import org.springframework.util.Assert;

/**
 * Base implementation for commands.
 */
public abstract class AbstractCommand implements ICommand
{
  private CodeType type;
  private boolean reachable;
  private int address;

  /**
   * Constructor.
   *
   * @param type the code type this command handles
   * @param reachable default value for reachability
   */
  protected AbstractCommand(CodeType type, boolean reachable)
  {
    this.type = type;
    this.reachable = reachable;
    this.address = -1;
  }

  @Override
  public CodeType getType()
  {
    return type;
  }

  @Override
  public boolean hasAddress()
  {
    return address >= 0;
  }

  @Override
  public int getAddress()
  {
    Assert.isTrue(hasAddress(), "Precondition: hasAddress()");
    return address;
  }

  @Override
  public void setAddress(int address)
  {
    Assert.isTrue(!hasAddress(), "Precondition: !hasAddress()");

    this.address = address;
  }

  @Override
  public final boolean isReachable()
  {
    return reachable;
  }

  @Override
  public final void setReachable(boolean reachable)
  {
    this.reachable = reachable;
  }
}

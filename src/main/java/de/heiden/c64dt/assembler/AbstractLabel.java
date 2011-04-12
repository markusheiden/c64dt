package de.heiden.c64dt.assembler;

import static de.heiden.c64dt.util.AddressUtil.assertValidAddress;
import static de.heiden.c64dt.util.HexUtil.*;

/**
 * Base implementation of labels.
 */
public abstract class AbstractLabel implements ILabel {
  private int address;

  /**
   * Constructor.
   *
   * @param address address the label points to.
   */
  public AbstractLabel(int address) {
    assertValidAddress(address);

    this.address = address;
  }

  public int getAddress() {
    return address;
  }

  @Override
  public int compareTo(ILabel label)
  {
    return getAddress() - label.getAddress();
  }

  public String toString() {
    return getLabelPrefix() + hexPlain(address);
  }

  /**
   * Prefix for label.
   */
  protected abstract String getLabelPrefix();
}

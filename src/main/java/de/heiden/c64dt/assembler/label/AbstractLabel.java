package de.heiden.c64dt.assembler.label;

import static de.heiden.c64dt.util.AddressUtil.assertValidAddress;
import static de.heiden.c64dt.util.HexUtil.hexPlain;

/**
 * Base implementation of labels.
 */
public abstract class AbstractLabel implements ILabel
{
  /**
   * Absolute address this label stands for.
   */
  private int address;

  /**
   * Constructor.
   *
   * @param address address the label points to.
   */
  public AbstractLabel(int address)
  {
    assertValidAddress(address);

    this.address = address;
  }

  @Override
  public int getAddress()
  {
    return address;
  }

  @Override
  public int compareTo(ILabel label)
  {
    return getAddress() - label.getAddress();
  }

  @Override
  public String toString(int address)
  {
    String result = getLabelPrefix() + "_" + hexPlain(this.address);
    if (this.address < address)
    {
      result += " + " + (address - this.address);
    }
    else if (this.address > address)
    {
      result += " - " + (this.address - address);
    }

    return result;
  }

  /**
   * Prefix for label.
   */
  protected abstract String getLabelPrefix();
}

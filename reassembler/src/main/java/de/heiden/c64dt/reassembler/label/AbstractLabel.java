package de.heiden.c64dt.reassembler.label;

import static de.heiden.c64dt.bytes.AddressUtil.assertValidAddress;
import static de.heiden.c64dt.bytes.HexUtil.hexPlain;

/**
 * Base implementation of labels.
 */
public abstract class AbstractLabel implements ILabel {
  /**
   * Absolute address this label stands for.
   */
  private final int address;

  /**
   * Constructor.
   *
   * @param address address the label points to.
   */
  public AbstractLabel(int address) {
    assertValidAddress(address);

    this.address = address;
  }

  @Override
  public int getAddress() {
    return address;
  }

  @Override
  public int compareTo(ILabel label) {
    return getAddress() - label.getAddress();
  }

  @Override
  public String toString() {
    return toString(this.address);
  }

  @Override
  public String toString(int address) {
    String result = getLabelPrefix() + "_" + hexPlain(this.address);
    if (this.address < address) {
      result += " + " + (address - this.address);
    } else if (this.address > address) {
      result += " - " + (this.address - address);
    }

    return result;
  }

  /**
   * Prefix for label.
   */
  protected abstract String getLabelPrefix();
}

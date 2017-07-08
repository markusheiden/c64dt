package de.heiden.c64dt.reassembler.label;

/**
 * Label for external references.
 */
public class ExternalLabel extends AbstractLabel {
  /**
   * Constructor.
   *
   * @param address address the label points to.
   */
  public ExternalLabel(int address) {
    super(address);
  }

  @Override
  protected String getLabelPrefix() {
    return getAddress() < 0x100 ? "Z" : "X";
  }
}

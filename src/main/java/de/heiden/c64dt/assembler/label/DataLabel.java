package de.heiden.c64dt.assembler.label;

/**
 * Label to data.
 */
public class DataLabel extends AbstractLabel {
  /**
   * Constructor.
   *
   * @param address address the label points to.
   */
  public DataLabel(int address) {
    super(address);
  }

  @Override
  protected String getLabelPrefix() {
    return "l";
  }
}

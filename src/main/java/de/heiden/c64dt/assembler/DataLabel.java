package main.java.de.heiden.c64dt.assembler;

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

  protected String getLabelPrefix() {
    return "l";
  }
}

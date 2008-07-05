package de.markusheiden.c64dt.assembler;

/**
 * Label to code.
 */
public class CodeLabel extends AbstractLabel {
  /**
   * Constructor.
   *
   * @param address address the label points to.
   */
  public CodeLabel(int address) {
    super(address);
  }

  protected String getLabelPrefix() {
    return "L";
  }
}

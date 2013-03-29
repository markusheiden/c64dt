package de.heiden.c64dt.assembler.label;

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

  @Override
  protected String getLabelPrefix() {
    return "L";
  }
}

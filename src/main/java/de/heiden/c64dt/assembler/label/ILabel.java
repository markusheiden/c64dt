package de.heiden.c64dt.assembler.label;

/**
 * Label.
 */
public interface ILabel extends Comparable<ILabel> {
  /**
   * Address the label points to.
   */
  public int getAddress();

  /**
   * String representation of this label.
   */
  public String toString();

  /**
   * String representation of this label.
   * +/- X will be added, if needed.
   *
   * @param address absolute address this label is used for
   */
  public String toString(int address);
}

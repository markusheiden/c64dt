package de.heiden.c64dt.assembler;

/**
 * Label.
 */
public interface ILabel {
  /**
   * Address the label points to.
   */
  public int getAddress();

  /**
   * String representation of this label.
   */
  public String toString();
}

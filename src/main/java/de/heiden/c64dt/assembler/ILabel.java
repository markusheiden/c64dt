package de.heiden.c64dt.assembler;

/**
 * Label.
 */
public interface ILabel extends Comparable<ILabel>
{
  /**
   * Address the label points to.
   */
  public int getAddress();

  /**
   * String representation of this label.
   */
  public String toString();
}

package de.heiden.c64dt.reassembler.label;

/**
 * Label.
 */
public interface ILabel extends Comparable<ILabel> {
  /**
   * Address the label points to.
   */
  int getAddress();

  /**
   * String representation of this label.
   */
  String toString();

  /**
   * String representation of this label.
   * +/- X will be added, if needed.
   *
   * @param address absolute address this label is used for
   */
  String toString(int address);
}

package main.java.de.heiden.c64dt.assembler;

/**
 * Types for code classification.
 */
public enum CodeType {
  /**
   * Type of code is not known yet.
   */
  UNKNOWN,

  /**
   * Declares that at this position should start an opcode.
   */
  OPCODE,

  /**
   * Declares that at this position is code (opcode or argument).
   */
  CODE,

  /**
   * Data.
   */
  DATA;

  public final boolean isCode() {
    return this == OPCODE || this == CODE;
  }

  public final boolean isData() {
    return this == DATA;
  }
}

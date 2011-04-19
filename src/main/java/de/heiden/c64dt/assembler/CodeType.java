package de.heiden.c64dt.assembler;

/**
 * Types for code classification.
 */
public enum CodeType
{
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
  DATA,

  /**
   * Bit to skip next command.
   */
  BIT,

  /**
   * Absolute Address.
   */
  ABSOLUTE_ADDRESS;

  public final boolean isUnknown() {
    return UNKNOWN.equals(this);
  }

  public final boolean isCode()
  {
    return this == OPCODE || this == CODE;
  }

  public final boolean isData()
  {
    return this == DATA;
  }
}

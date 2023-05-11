package de.heiden.c64dt.assembler;

import jakarta.xml.bind.annotation.XmlEnum;

/**
 * Types for code classification.
 */
@XmlEnum
public enum CodeType {
  /**
   * Type of code is not known yet.
   */
  UNKNOWN("U"),

  /**
   * Declares that at this position should start an opcode.
   */
  OPCODE("O"),

  /**
   * Declares that at this position is code (opcode or argument).
   */
  CODE("C"),

  /**
   * Data.
   */
  DATA("D"),

  /**
   * Bit to skip next command.
   */
  BIT("B"),

  /**
   * Absolute Address.
   */
  ADDRESS("A");

  /**
   * ID.
   */
  private final String id;

  /**
   * Constructor.
   *
   * @param id ID
   */
  CodeType(String id) {
    this.id = id;
  }

  /**
   * ID.
   */
  public String getId() {
    return id;
  }

  /**
   * Is the code type unknown?.
   */
  public final boolean isUnknown() {
    return UNKNOWN.equals(this);
  }

  /**
   * Is it code?.
   */
  public final boolean isCode() {
    return this == OPCODE || this == CODE;
  }

  /**
   * Is it data?.
   */
  public final boolean isData() {
    return this == DATA || this == ADDRESS;
  }
}

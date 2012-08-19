package de.heiden.c64dt.assembler.command;

import de.heiden.c64dt.assembler.CodeType;

/**
 * Description of a subroutine.
 */
public class Subroutine {
  /**
   * Absolute address of subroutine.
   */
  private final int address;

  /**
   * Number of bytes which follow the jsr as argument.
   */
  private final int arguments;

  /**
   * Code type of argument bytes.
   */
  private final CodeType type;

  /**
   * Constructor.
   *
   * @param address Absolute address of subroutine
   * @param arguments Number of bytes which follow the jsr as argument
   */
  public Subroutine(int address, int arguments) {
    this(address, arguments, CodeType.DATA);
  }

  /**
   * Constructor.
   *
   * @param address Absolute address of subroutine
   * @param arguments Number of bytes which follow the jsr as argument
   * @param type Code type of argument
   */
  public Subroutine(int address, int arguments, CodeType type) {
    this.address = address;
    this.arguments = arguments;
    this.type = type;
  }

  /**
   * Relative address of subroutine.
   */
  public int getAddress() {
    return address;
  }

  /**
   * Number of bytes which follow the jsr as argument.
   */
  public int getArguments() {
    return arguments;
  }

  /**
   * Code type of arguments bytes.
   */
  public CodeType getType() {
    return type;
  }
}

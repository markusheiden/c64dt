package de.heiden.c64dt.assembler.command;

import de.heiden.c64dt.assembler.CodeType;

/**
 * Description of a soubroutine.
 */
public class Subroutine
{
  /**
   * Relative address of subroutine.
   */
  private final int index;

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
   * @param index Relative address of subroutine
   * @param arguments Number of bytes which follow the jsr as argument
   */
  public Subroutine(int index, int arguments)
  {
    this(index, arguments, CodeType.DATA);
  }

  /**
   * Constructor.
   *
   * @param index Relative address of subroutine
   * @param arguments Number of bytes which follow the jsr as argument
   * @param type Code type of argument
   */
  public Subroutine(int index, int arguments, CodeType type)
  {
    this.index = index;
    this.arguments = arguments;
    this.type = type;
  }

  /**
   * Relative address of subroutine.
   */
  public int getIndex()
  {
    return index;
  }

  /**
   * Number of bytes which follow the jsr as argument.
   */
  public int getArguments()
  {
    return arguments;
  }

  /**
   * Code type of arguments bytes.
   */
  public CodeType getType()
  {
    return type;
  }
}

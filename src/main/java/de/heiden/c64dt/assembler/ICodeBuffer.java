package de.heiden.c64dt.assembler;

/**
 * Created by IntelliJ IDEA.
 * User: markus
 * Date: Oct 9, 2010
 * Time: 1:50:43 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ICodeBuffer
{
  void restart();

  int getCommandAddress();

  int getCurrentAddress();

  boolean hasAddress(int address);

  int getStartAddress();

  int getEndAddress();

  boolean has(int number);

  /**
   * Read a byte from the code at the current position and advance.
   */
  int readByte();

  Opcode readOpcode();

  int readRelative();

  int readAbsolute(int number);

  CodeType getType();

  void setType(int address, CodeType type);
}

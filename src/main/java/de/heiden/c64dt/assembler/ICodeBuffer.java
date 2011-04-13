package de.heiden.c64dt.assembler;

/**
 * Interface for code buffers.
 */
public interface ICodeBuffer
{
  /**
   * Restart.
   * Sets the current position to the start of the code / commands.
   */
  public void restart();

  /**
   * The index of the current command.
   */
  public int getCommandIndex();

  /**
   * The current index.
   */
  public int getCurrentIndex();

  //
  // code specific interface
  //

  /**
   * Are there 'number' bytes left to read?
   *
   * @param number number of bytes
   */
  public boolean has(int number);

  /**
   * Read a byte from the code at the current position and advance.
   */
  public int readByte();

  /**
   * Read an opcode.
   */
  public Opcode readOpcode();

  /**
   * Read 'number' bytes as one word.
   *
   * @param number number of bytes to read
   */
  public int read(int number);
}

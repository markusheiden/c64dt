package de.heiden.c64dt.assembler;

/**
 * Interface for code buffers.
 */
public interface ICodeBuffer {
  /**
   * The address of the current command.
   */
  public int getCommandAddress();

  /**
   * The index of the current command.
   */
  public int getCommandIndex();

  /**
   * The current address.
   */
  public int getCurrentAddress();

  /**
   * Set the current address.
   *
   * @param address address
   */
  public void setCurrentAddress(int address);

  /**
   * The current index.
   */
  public int getCurrentIndex();

  /**
   * Set the current index.
   *
   * @param index index
   */
  public void setCurrentIndex(int index);

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
   * Read an opcode.
   */
  public Opcode readOpcode();

  /**
   * Read 'number' bytes as one word.
   *
   * @param number number of bytes to read
   * @return read byte if number == 1, read word if number == 2, -1 if number == 0
   */
  public int read(int number);

  /**
   * Read a byte from the code at the current position and advance.
   */
  public int readByte();

  /**
   * Read a word from the code at the current position and advance.
   */
  public int readWord();
}

package de.heiden.c64dt.assembler;

/**
 * Interface for code buffers.
 */
public interface ICodeBuffer {
  /**
   * The address of the current command.
   */
  int getCommandAddress();

  /**
   * The index of the current command.
   */
  int getCommandIndex();

  /**
   * The current address.
   */
  int getCurrentAddress();

  /**
   * Set the current address.
   *
   * @param address Address.
   */
  void setCurrentAddress(int address);

  /**
   * The current index.
   */
  int getCurrentIndex();

  /**
   * Set the current index.
   *
   * @param index Index.
   */
  void setCurrentIndex(int index);

  //
  // Code specific interface.
  //

  /**
   * Is there at least one byte left to read?
   */
  boolean hasMore();

  /**
   * Are there 'number' bytes left to read?
   *
   * @param number Number of bytes.
   */
  boolean has(int number);

  /**
   * Read an opcode.
   */
  Opcode readOpcode();

  /**
   * Read 'number' bytes as one word.
   *
   * @param number Number of bytes to read.
   * @return Read byte if number == 1, read word if number == 2, -1 if number == 0.
   */
  int read(int number);

  /**
   * Read a byte from the code at the current position and advance.
   */
  int readByte();

  /**
   * Read a word from the code at the current position and advance.
   */
  int readWord();
}

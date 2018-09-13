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
   * @param address address
   */
  void setCurrentAddress(int address);

  /**
   * The current index.
   */
  int getCurrentIndex();

  /**
   * Set the current index.
   *
   * @param index index
   */
  void setCurrentIndex(int index);

  //
  // code specific interface
  //

  /**
   * Is there at least one byte left to read?
   */
  boolean hasMore();

  /**
   * Are there 'number' bytes left to read?
   *
   * @param number number of bytes
   */
  boolean has(int number);

  /**
   * Read an opcode.
   */
  Opcode readOpcode();

  /**
   * Read 'number' bytes as one word.
   *
   * @param number number of bytes to read
   * @return read byte if number == 1, read word if number == 2, -1 if number == 0
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

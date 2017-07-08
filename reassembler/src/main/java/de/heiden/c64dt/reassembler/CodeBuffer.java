package de.heiden.c64dt.reassembler;

import de.heiden.c64dt.assembler.AbstractCodeBuffer;
import de.heiden.c64dt.bytes.ByteUtil;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;

import static de.heiden.c64dt.bytes.ByteUtil.toWord;

/**
 * Input stream for code.
 */
public class CodeBuffer extends AbstractCodeBuffer {
  /**
   * The code.
   */
  private final byte[] code;

  /**
   * Constructor.
   *
   * @param address start address of the code
   * @param code code
   */
  public CodeBuffer(int address, byte[] code) {
    super(address, code.length);

    this.code = code;
  }

  @Override
  protected int readByteAt(int index) {
    return ByteUtil.toByte(code[index]);
  }

  /**
   * The code.
   */
  public byte[] getCode() {
    return code;
  }

  //
  // Factories
  //

  /**
   * Create code buffer from program.
   * The first two bytes are used as start address.
   *
   * @param startAddr Start address of code
   * @param code Code
   */
  public static CodeBuffer fromCode(int startAddr, InputStream code) throws IOException {
    Assert.isTrue(startAddr >= 0, "Precondition: startAddr >= 0");
    Assert.notNull(code, "Precondition: code != null");

    return new CodeBuffer(startAddr, FileCopyUtils.copyToByteArray(code));
  }

  /**
   * Create code buffer from program.
   * The first two bytes are used as start address.
   *
   * @param program program with start address
   */
  public static CodeBuffer fromProgram(InputStream program) throws IOException {
    Assert.notNull(program, "Precondition: program != null");

    return fromProgram(FileCopyUtils.copyToByteArray(program));
  }

  /**
   * Create code buffer from program.
   * The first two bytes are used as start address.
   *
   * @param program program with start address
   */
  public static CodeBuffer fromProgram(byte[] program) throws IOException {
    Assert.notNull(program, "Precondition: program != null");
    Assert.isTrue(program.length >= 2, "Precondition: program.length >= 2");

    int address = toWord(program, 0);
    byte[] code = new byte[program.length - 2];
    System.arraycopy(program, 2, code, 0, code.length);
    return new CodeBuffer(address, code);
  }
}

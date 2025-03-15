package de.heiden.c64dt.assembler;

import java.io.IOException;
import java.io.InputStream;

import de.heiden.c64dt.bytes.ByteUtil;
import org.apache.commons.io.IOUtils;

import static com.github.cowwoc.requirements10.java.DefaultJavaValidators.requireThat;
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
    requireThat(startAddr, "startAddr").isGreaterThanOrEqualTo(0);
    requireThat(code, "code").isNotNull();

    return new CodeBuffer(startAddr, IOUtils.toByteArray(code));
  }

  /**
   * Create code buffer from program.
   * The first two bytes are used as start address.
   *
   * @param program program with start address
   */
  public static CodeBuffer fromProgram(InputStream program) throws IOException {
    requireThat(program, "program").isNotNull();

    return fromProgram(IOUtils.toByteArray(program));
  }

  /**
   * Create code buffer from program.
   * The first two bytes are used as start address.
   *
   * @param program program with start address
   */
  public static CodeBuffer fromProgram(byte[] program) throws IOException {
    requireThat(program, "program").isNotNull();
    requireThat(program.length, "program.length").isGreaterThanOrEqualTo(2);

    int address = toWord(program, 0);
    byte[] code = new byte[program.length - 2];
    System.arraycopy(program, 2, code, 0, code.length);
    return new CodeBuffer(address, code);
  }
}

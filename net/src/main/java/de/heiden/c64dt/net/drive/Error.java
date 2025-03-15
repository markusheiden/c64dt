package de.heiden.c64dt.net.drive;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import de.heiden.c64dt.charset.C64Charset;

import static com.github.cowwoc.requirements10.java.DefaultJavaValidators.requireThat;

/**
 * Error constants.
 */
public enum Error {
  OK(0x00, "", 0x00),
  PROTECTED(0x01, "", 0x01),
  NOTFOUND(62, "FILE NOT FOUND", 0x04),
  DIRERROR(0x03, "", 0x03),
  FILEOPEN(60, "WRITE FILE OPEN", 0x02),
  FILENOTOPEN(61, "FILE NOT OPEN", 0x03),
  EXISTS(63, "FILE EXISTS", 0x00),
  SYNTAX31(31, "SYNTAX ERROR", 0x00),
  SYNTAX33(33, "SYNTAX ERROR", 0x00);

  private static final NumberFormat FORMAT = new DecimalFormat("##");

  private final byte code;
  private final String description;
  private final byte result;

  /**
   * Constructor.
   *
   * @param code error code
   */
  Error(int code, String description, int result) {
    requireThat(description, "description").isNotNull();

    this.code = (byte) code;
    this.description = description;
    this.result = (byte) result;
  }

  /**
   * Error code.
   */
  public byte getCode() {
    return code;
  }

  /**
   * Error description.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Error result code.
   */
  public byte getResult() {
    return result;
  }

  public String toString() {
    return FORMAT.format(code) + ", " + description;
  }

  /**
   * Complete error line.
   *
   * @param track track
   * @param sector sector
   */
  public String toString(int track, int sector) {
    return toString() + "," + FORMAT.format(track) + "," + FORMAT.format(sector);
  }

  /**
   * Complete error line in C64 encoding.
   *
   * @param track track
   * @param sector sector
   */
  public byte[] toBytes(int track, int sector) {
    return C64Charset.LOWER.toBytes(toString(track, sector));
  }
}

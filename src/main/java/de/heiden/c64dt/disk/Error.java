package de.heiden.c64dt.disk;

import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * D64 error codes.
 */
public enum Error {
  /**
   * No error recorded.
   */
  OK(0x00),

  /**
   * No error detected.
   */
  NO_ERROR(0x01),

  /**
   * Header description byte not found.
   */
  HEADER_DESCRIPTION_BYTE_NOT_FOUND(0x02),

  /**
   * No sync sequence found.
   */
  NO_SYNC_SEQUENCE_FOUND(0x03),

  /**
   * Data descriptor byte not found.
   */
  DATA_DESCRIPTOR_BYTE_NOT_FOUND(0x04),

  /**
   * Checksum error in data block.
   */
  CHECKSUM_ERROR_IN_DATA_BLOCK(0x05),

  /**
   * Write verify (on format).
   */
  WRITE_VERIFY_ON_FORMAT(0x06),

  /**
   * Write verify error.
   */
  WRITE_VERIFY_ERROR(0x07),

  /**
   * Write protect on.
   */
  WRITE_PROTECT_ON(0x08),

  /**
   * Checksum error in header block.
   */
  CHECKSUM_ERROR_IN_HEADER_BLOCK(0x09),

  /**
   * Write error.
   */
  WRITE_ERROR(0x0A),

  /**
   * Disc sector id mismatch.
   */
  DISK_SECTOR_ID_MISMATCH(0x0B),

  /**
   * Drive not ready.
   */
  DRIVE_NOT_READY(0x0F),

  /**
   * Undefined value, which is assigned to all not defined error codes.
   */
  UNKNOWN(0xFF);

  private static final Logger logger = Logger.getLogger(Error.class);

  private static Map<Byte, Error> errors;

  private byte error;

  /**
   * Get error code.
   */
  public byte getError() {
    return error;
  }

  /**
   * Represents this error an error.
   */
  public boolean isError() {
    return !this.equals(OK) && !this.equals(NO_ERROR);
  }

  /**
   * Get error for error code.
   *
   * @param error error code
   */
  public static Error error(int error) {
    Error result = errors.get((byte) error);
    if (result == null) {
      logger.info("Unknow error code " + Integer.toHexString(error));
      result = UNKNOWN;
    }

    Assert.notNull(result, "Postcondition: result != null");
    return result;
  }

  /**
   * Constructor.
   *
   * @param error error code
   */
  private Error(int error) {
    this.error = (byte) error;
    errors().put((byte) error, this);
  }

  /**
   * Map with all known error instances.
   */
  private Map<Byte, Error> errors() {
    if (errors == null) {
      errors = new HashMap<>();
    }

    return errors;
  }
}

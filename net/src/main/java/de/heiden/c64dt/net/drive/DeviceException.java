package de.heiden.c64dt.net.drive;

/**
 * Exception for device operations.
 */
public class DeviceException extends Exception {
  private final Error error;

  public DeviceException(Error error) {
    super(error.toString());

    this.error = error;
  }

  /**
   * Error.
   */
  public Error getError() {
    return error;
  }
}

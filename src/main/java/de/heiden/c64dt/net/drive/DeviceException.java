package de.heiden.c64dt.net.drive;

import org.springframework.util.Assert;

/**
 * Exception for device operations.
 */
public class DeviceException extends Exception {
  private Error error;

  public DeviceException(Error error) {
    super(error.toString());

    this.error = error;
  }

  /**
   * Error.
   */
  public Error getError() {
    Assert.notNull(error, "Postcondition: result != null");
    return error;
  }
}

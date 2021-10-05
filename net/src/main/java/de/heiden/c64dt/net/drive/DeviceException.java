package de.heiden.c64dt.net.drive;

import static de.heiden.c64dt.common.Requirements.R;

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
    R.requireThat(error, "result").isNotNull();
    return error;
  }
}

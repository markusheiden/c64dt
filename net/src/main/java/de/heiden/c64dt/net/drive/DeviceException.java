package de.heiden.c64dt.net.drive;

import static com.github.cowwoc.requirements10.java.DefaultJavaValidators.requireThat;

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
    requireThat(error, "result").isNotNull();
    return error;
  }
}

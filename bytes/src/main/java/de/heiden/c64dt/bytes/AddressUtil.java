package de.heiden.c64dt.bytes;

import static com.github.cowwoc.requirements10.java.DefaultJavaValidators.requireThat;

/**
 * Helper for handling address.
 */
public class AddressUtil {
  /**
   * Check validity of a given address.
   */
  public static void requireValidAddress(int address) {
    requireThat(address, "address").isGreaterThanOrEqualTo(0x0000).isLessThanOrEqualTo(0xFFFF);
  }
}

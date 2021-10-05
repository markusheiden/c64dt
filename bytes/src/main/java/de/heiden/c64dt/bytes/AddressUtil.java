package de.heiden.c64dt.bytes;

import static de.heiden.c64dt.common.Requirements.R;

/**
 * Helper for handling address.
 */
public class AddressUtil {
  /**
   * Check validity of a given address.
   */
  public static void requireValidAddress(int address) {
    R.requireThat(address, "address").isGreaterThanOrEqualTo(0x0000).isLessThanOrEqualTo(0xFFFF);
  }
}

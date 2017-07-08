package de.heiden.c64dt.bytes;

import org.springframework.util.Assert;

/**
 * Helper for handling address.
 */
public class AddressUtil {
  /**
   * Check validity of a given address.
   */
  public static void assertValidAddress(int address) {
    Assert.isTrue(address >= 0 && address <= 0xFFFF, "Precondition: address >= 0 && address <= 0xFFFF");
  }
}

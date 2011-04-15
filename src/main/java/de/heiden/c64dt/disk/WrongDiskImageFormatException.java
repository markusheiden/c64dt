package de.heiden.c64dt.disk;

/**
 * Exception thrown when an unknown disk image format is detected.
 */
public class WrongDiskImageFormatException extends Exception
{
  public WrongDiskImageFormatException(int bytes)
  {
    super("Unknown data format with " + bytes + " bytes");
  }
}

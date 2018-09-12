package de.heiden.c64dt.disk;

/**
 * File.
 */
public interface IFile {
  /**
   * Get file mode.
   */
  FileMode getMode();

  /**
   * Get starting track.
   */
  int getTrack();

  /**
   * Get starting sector.
   */
  int getSector();

  /**
   * Get name in C64 encoding.
   */
  byte[] getName();

  /**
   * File size in sectors.
   */
  int getSize();
}

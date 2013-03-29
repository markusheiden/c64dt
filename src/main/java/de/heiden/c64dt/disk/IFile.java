package de.heiden.c64dt.disk;

/**
 * File.
 */
public interface IFile {
  /**
   * Get file mode.
   */
  public FileMode getMode();

  /**
   * Get starting track.
   */
  public int getTrack();

  /**
   * Get starting sector.
   */
  public int getSector();

  /**
   * Get name in C64 encoding.
   */
  public byte[] getName();

  /**
   * File size in sectors.
   */
  public int getSize();
}

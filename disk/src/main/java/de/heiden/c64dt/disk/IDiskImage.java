package de.heiden.c64dt.disk;

/**
 * Disk image.
 */
public interface IDiskImage extends ISectorModel {
  /**
   * Get sector content.
   *
   * @param track Track (1-based).
   * @param sector Sector (0-based).
   */
  byte[] getSector(int track, int sector);

  /**
   * Set sector content.
   *
   * @param track Track (1-based).
   * @param sector Sector (0-based).
   * @param content Sector content.
   */
  void setSector(int track, int sector, byte[] content);

  /**
   * Does this image have error information?
   */
  boolean hasErrors();

  /**
   * Get if sector has an error.
   *
   * @param track Track (1-based).
   * @param sector Sector (0-based).
   */
  boolean hasError(int track, int sector);

  /**
   * Get error for a sector.
   *
   * @param track Track (1-based).
   * @param sector Sector (0-based).
   */
  Error getError(int track, int sector);

  /**
   * Set error for a sector.
   *
   * @param track Track (1-based).
   * @param sector Sector (0-based).
   * @param error Error.
   */
  void setError(int track, int sector, Error error);

  /**
   * Read the BAM.
   */
  IBAM getBAM();

  /**
   * Read the directory.
   */
  IDirectory getDirectory();
}

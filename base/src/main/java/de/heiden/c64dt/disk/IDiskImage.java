package de.heiden.c64dt.disk;

/**
 * Disk image.
 */
public interface IDiskImage extends ISectorModel {
  /**
   * Get sector content.
   *
   * @param track track (1-based)
   * @param sector sector (0-based)
   */
  public byte[] getSector(int track, int sector);

  /**
   * Set sector content.
   *
   * @param track track (1-based)
   * @param sector sector (0-based)
   * @param content sector content
   */
  public void setSector(int track, int sector, byte[] content);

  /**
   * Does this image have error informations?
   */
  public boolean hasErrors();

  /**
   * Get if sector has an error.
   *
   * @param track track (1-based)
   * @param sector sector (0-based)
   */
  public boolean hasError(int track, int sector);

  /**
   * Get error for a sector.
   *
   * @param track track (1-based)
   * @param sector sector (0-based)
   */
  public Error getError(int track, int sector);

  /**
   * Set error for a sector.
   *
   * @param track track (1-based)
   * @param sector sector (0-based)
   * @param error error
   */
  public void setError(int track, int sector, Error error);

  /**
   * Read the bam.
   */
  public IBAM getBAM();

  /**
   * Read the directory.
   */
  public IDirectory getDirectory();
}

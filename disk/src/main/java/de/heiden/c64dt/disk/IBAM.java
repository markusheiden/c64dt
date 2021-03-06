package de.heiden.c64dt.disk;

/**
 * Block allocation map implementation.
 */
public interface IBAM {
  /**
   * Is the sector marked as free?
   *
   * @param track track
   * @param sector sector
   */
  boolean isFree(int track, int sector);

  /**
   * Mark the sector as free or used.
   *
   * @param track track
   * @param sector sector
   * @param isFree mark as free
   */
  void setFree(int track, int sector, boolean isFree);

  /**
   * Get total number of free sectors.
   */
  int getFreeSectors();


  /**
   * Get number of free sectors of a track.
   *
   * @param track track
   */
  int getFreeSectors(int track);

  /**
   * Set number of free sectors of a track.
   *
   * @param track track
   * @param freeSectors free sectors
   */
  void setFreeSectors(int track, int freeSectors);
}

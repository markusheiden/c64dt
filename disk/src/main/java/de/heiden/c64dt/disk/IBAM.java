package de.heiden.c64dt.disk;

/**
 * Block allocation map implementation.
 */
public interface IBAM {
  /**
   * Is the sector marked as free?
   *
   * @param track Track.
   * @param sector Sector.
   */
  boolean isFree(int track, int sector);

  /**
   * Mark the sector as free or used.
   *
   * @param track Track.
   * @param sector Sector.
   * @param isFree Mark as free.
   */
  void setFree(int track, int sector, boolean isFree);

  /**
   * Get total number of free sectors.
   */
  int getFreeSectors();


  /**
   * Get number of free sectors of a track.
   *
   * @param track Track.
   */
  int getFreeSectors(int track);

  /**
   * Set number of free sectors of a track.
   *
   * @param track Track.
   * @param freeSectors Free sectors.
   */
  void setFreeSectors(int track, int freeSectors);
}

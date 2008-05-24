package de.markusheiden.c64dt.disk;

/**
 * Value type for a sector model.
 */
public interface ISectorModel {
  /**
   * Number of sides.
   */
  public int getSides();

  /**
   * Number of tracks.
   */
  public int getTracks();

  /**
   * Number of tracks per side.
   */
  public int getTracksPerSide();

  /**
   * Maximum number of sectors.
   */
  public int getSectors();

  /**
   * Number of sectors of a given track.
   *
   * @param track Track
   */
  public int getSectors(int track);
}

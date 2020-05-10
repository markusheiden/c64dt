package de.heiden.c64dt.disk;

/**
 * Value type for a sector model.
 */
public interface ISectorModel {
  /**
   * Number of sides.
   */
  int getSides();

  /**
   * Number of tracks.
   */
  int getTracks();

  /**
   * Number of tracks per side.
   */
  int getTracksPerSide();

  /**
   * Maximum number of sectors.
   */
  int getSectors();

  /**
   * Number of sectors of a given track.
   *
   * @param track Track
   */
  int getSectors(int track);
}

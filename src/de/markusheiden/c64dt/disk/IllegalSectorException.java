package de.markusheiden.c64dt.disk;

/**
 * Exception thrown when an invalid track or sector number is detected.
 */
public class IllegalSectorException extends IllegalArgumentException {
  /**
   * Constructor for illegal tracks.
   *
   * @param track track
   */
  public IllegalSectorException(int track) {
    super("Illegal track " + track);
  }

  /**
   * Constructor for illegal sectors.
   *
   * @param track track
   * @param sector sector
   */
  public IllegalSectorException(int track, int sector) {
    super("Illegal sector " + track + " / " + sector);
  }
}

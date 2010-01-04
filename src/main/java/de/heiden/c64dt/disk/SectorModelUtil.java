package main.java.de.heiden.c64dt.disk;

/**
 * Util class for checking preconditions based on a sector model.
 */
public class SectorModelUtil {
  public static void assertTrack (ISectorModel sectorModel, int track) {
    if (track < 1 || track > sectorModel.getTracks()) {
      throw new IllegalSectorException(track);
    }
  }

  public static void assertSector (ISectorModel sectorModel, int track, int sector) {
    assertTrack(sectorModel, track);
    if (sector < 0 || sector >= sectorModel.getSectors(track)) {
      throw new IllegalSectorException(track, sector);
    }
  }
}

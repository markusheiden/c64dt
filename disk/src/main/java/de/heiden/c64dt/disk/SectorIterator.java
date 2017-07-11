package de.heiden.c64dt.disk;

import de.heiden.c64dt.bytes.ByteUtil;

import java.util.Iterator;

import static de.heiden.c64dt.disk.SectorModelUtil.requireValidSector;
import static org.bitbucket.cowwoc.requirements.core.Requirements.requireThat;

/**
 * Reads a chain of sectors.
 */
public class SectorIterator implements Iterator<byte[]> {
  private final IDiskImage diskImage;
  private int track;
  private int sector;

  /**
   * Constructor.
   *
   * @param diskImage disk image
   * @param track start track
   * @param sector start sector
   */
  public SectorIterator(IDiskImage diskImage, int track, int sector) {
    requireThat(diskImage, "diskImage").isNotNull();
    requireValidSector(diskImage, track, sector);

    this.diskImage = diskImage;
    this.track = track;
    this.sector = sector;
  }

  @Override
  public boolean hasNext() {
    return track != 0x00;
  }

  @Override
  public byte[] next() {
    requireThat(hasNext(), "hasNext()").isTrue();

    byte[] currentSector = diskImage.getSector(track, sector);
    track = ByteUtil.toByte(currentSector[0]);
    sector = ByteUtil.toByte(currentSector[1]);

    requireThat(currentSector, "result").isNotNull();
    return currentSector;
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }
}

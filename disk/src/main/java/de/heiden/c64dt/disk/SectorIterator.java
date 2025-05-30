package de.heiden.c64dt.disk;

import java.util.Iterator;

import de.heiden.c64dt.bytes.ByteUtil;

import static com.github.cowwoc.requirements10.java.DefaultJavaValidators.requireThat;
import static de.heiden.c64dt.disk.SectorModelUtil.requireValidSector;

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

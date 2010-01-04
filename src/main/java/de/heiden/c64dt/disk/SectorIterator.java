package de.heiden.c64dt.disk;

import de.heiden.c64dt.util.ByteUtil;
import org.springframework.util.Assert;

import java.util.Iterator;

import static de.heiden.c64dt.disk.SectorModelUtil.assertSector;

/**
 * Reads a chain of sectors.
 */
public class SectorIterator implements Iterator<byte[]> {
  private IDiskImage diskImage;
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
    Assert.notNull(diskImage, "Precondition: diskImage != null");
    assertSector(diskImage, track, sector);

    this.diskImage = diskImage;
    this.track = track;
    this.sector = sector;
  }

  public boolean hasNext() {
    return track != 0x00;
  }

  public byte[] next() {
    Assert.isTrue(hasNext(), "Precondition: hasNext()");

    byte[] currentSector = diskImage.getSector(track, sector);
    track = ByteUtil.toByte(currentSector[0]);
    sector =  ByteUtil.toByte(currentSector[1]);

    Assert.notNull(currentSector, "Postcondition: result != null");
    return currentSector;
  }

  public void remove() {
    throw new UnsupportedOperationException();
  }
}

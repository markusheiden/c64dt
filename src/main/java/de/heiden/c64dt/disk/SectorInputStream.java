package main.java.de.heiden.c64dt.disk;

import static main.java.de.heiden.c64dt.disk.SectorModelUtil.assertSector;
import main.java.de.heiden.c64dt.util.ByteUtil;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;

/**
 * Reads a file from a chain of sectors.
 */
public class SectorInputStream extends InputStream {
  private IDiskImage diskImage;
  private int track;
  private int sector;

  private byte[] currentSector;
  private int pos;
  private int remaining;

  /**
   * Constructor.
   *
   * @param diskImage disk image
   * @param file file
   */
  public SectorInputStream(IDiskImage diskImage, IFile file) {
    this(diskImage, file.getTrack(), file.getSector());
  }

  /**
   * Constructor.
   *
   * @param diskImage disk image
   * @param track start track
   * @param sector start sector
   */
  public SectorInputStream(IDiskImage diskImage, int track, int sector) {
    Assert.notNull(diskImage, "Precondition: diskImage != null");
    assertSector(diskImage, track, sector);

    this.diskImage = diskImage;
    this.track = track;
    this.sector = sector;

    this.currentSector = null;
    this.pos = 0;
    this.remaining = 0;
  }

  public int read() throws IOException {
    if (remaining == 0) {
      if (track == 0x00) {
        return -1;
      }

      currentSector = diskImage.getSector(track, sector);
      track = ByteUtil.toByte(currentSector[0]);
      sector =  ByteUtil.toByte(currentSector[1]);
      pos = 0x02;
      remaining = track == 0x00? sector : 0xFE;
    }

    --remaining;
    return ByteUtil.toByte(currentSector[pos++]);
  }
}

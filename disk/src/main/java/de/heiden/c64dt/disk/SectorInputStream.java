package de.heiden.c64dt.disk;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import de.heiden.c64dt.bytes.ByteUtil;
import org.jspecify.annotations.Nullable;

import static de.heiden.c64dt.disk.SectorModelUtil.requireValidSector;

/**
 * Reads a file from a chain of sectors.
 */
public class SectorInputStream extends InputStream {
  private final IDiskImage diskImage;
  private int track;
  private int sector;

  private byte @Nullable [] currentSector;
  private int pos;
  private int remaining;

  /**
   * Constructor.
   *
   * @param diskImage Disk image.
   * @param file File.
   */
  public SectorInputStream(IDiskImage diskImage, IFile file) {
    this(diskImage, file.getTrack(), file.getSector());
  }

  /**
   * Constructor.
   *
   * @param diskImage Disk image.
   * @param track Start track.
   * @param sector Start sector.
   */
  public SectorInputStream(IDiskImage diskImage, int track, int sector) {
    requireValidSector(diskImage, track, sector);

    this.diskImage = diskImage;
    this.track = track;
    this.sector = sector;

    this.currentSector = null;
    this.pos = 0;
    this.remaining = 0;
  }

  @Override
  public int read() throws IOException {
    if (remaining == 0) {
      if (track == 0x00) {
        return -1;
      }

      var nextSector = diskImage.getSector(track, sector);
      currentSector = nextSector;
      track = ByteUtil.toByte(nextSector[0]);
      sector = ByteUtil.toByte(nextSector[1]);
      pos = 0x02;
      remaining = track == 0x00 ? sector : 0xFE;
    }

    --remaining;
    // remaining is 0 until the first sector has been read, so the current sector is set here.
    return ByteUtil.toByte(Objects.requireNonNull(currentSector)[pos++]);
  }
}

package de.heiden.c64dt.disk;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.heiden.c64dt.bytes.ByteUtil;
import de.heiden.c64dt.charset.TextUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.cowwoc.requirements10.java.DefaultJavaValidators.requireThat;
import static de.heiden.c64dt.disk.SectorModelUtil.requireValidSector;

/**
 * Abstract disk image implementation.
 */
public abstract class AbstractDiskImage implements IDiskImage {
  /**
   * Logger.
   */
  private final Logger log = LoggerFactory.getLogger(getClass());

  private final int sides;
  private final int tracks;
  private final int tracksPerSide;
  private final boolean hasErrors;
  private final byte[][][] sectors;
  private final Error[][] errors;

  /**
   * Constructor.
   *
   * @param sides number of disk sides
   * @param tracks number of tracks
   * @param hasErrors support error informations?
   */
  protected AbstractDiskImage(int sides, int tracks, boolean hasErrors) {
    requireThat(sides, "sides").isGreaterThanOrEqualTo(1).isLessThanOrEqualTo(2);
    requireThat(tracks, "tracks").isGreaterThanOrEqualTo(0);
    requireThat(tracks % sides, "tracks % sides").isEqualTo(0);

    this.sides = sides;
    this.tracks = tracks;
    this.tracksPerSide = tracks / sides;
    this.hasErrors = hasErrors;

    sectors = new byte[tracks][][];
    errors = new Error[tracks][];
    for (int track = 1; track <= tracks; track++) {
      int spt = getSectors(track);
      sectors[track - 1] = new byte[spt][];
      errors[track - 1] = new Error[spt];
      for (int sector = 0; sector < spt; sector++) {
        sectors[track - 1][sector] = new byte[256];
        errors[track - 1][sector] = Error.NO_ERROR;
      }
    }
  }

  /**
   * Load D64 from binary representation.
   */
  public void load(InputStream stream) throws IOException {
    byte[] bytes = IOUtils.toByteArray(stream);
    for (int pos = 0, track = 1; track <= getTracks(); track++) {
      for (int sector = 0; sector < getSectors(track) && pos < bytes.length; sector++, pos += 256) {
        log.info("Reading track {} sector {}.", track, sector);
        System.arraycopy(bytes, pos, sectors[track - 1][sector], 0, 256);
      }
    }
  }

  //
  // ISectorModel
  //

  @Override
  public int getSides() {
    return sides;
  }

  @Override
  public int getTracks() {
    return tracks;
  }

  @Override
  public int getTracksPerSide() {
    return tracksPerSide;
  }

  //
  //
  //

  @Override
  public byte[] getSector(int track, int sector) {
    requireValidSector(this, track, sector);

    return sectors[track - 1][sector];
  }

  @Override
  public void setSector(int track, int sector, byte[] content) {
    requireValidSector(this, track, sector);

    if (content.length != 256) {
      throw new IllegalArgumentException("Illegal sector content");
    }

    System.arraycopy(content, 0, sectors[track - 1][sector], 0, 256);
  }

  //
  // error support
  //

  @Override
  public boolean hasErrors() {
    return hasErrors;
  }

  @Override
  public boolean hasError(int track, int sector) {
    return hasErrors() && getError(track, sector).isError();
  }

  @Override
  public Error getError(int track, int sector) {
    requireThat(hasErrors(), "hasErrors()").isTrue();
    requireValidSector(this, track, sector);

    return errors[track - 1][sector];
  }

  @Override
  public void setError(int track, int sector, Error error) {
    requireThat(hasErrors(), "hasErrors()").isTrue();
    requireValidSector(this, track, sector);
    requireThat(error, "error").isNotNull();

    errors[track - 1][sector] = error;
  }

  //
  // BAM, directory
  //

  /**
   * Size of a bam track entry in bytes.
   */
  protected abstract int getBamEntrySize();

  /**
   * Read a track entry.
   *
   * @param bam bam to fill
   * @param track current track
   * @param content sector buffer
   * @param pos position in sector buffer
   */
  protected void readBAM(IBAM bam, int track, byte[] content, int pos) {
    requireThat(bam, "bam").isNotNull();
    requireThat(content, "content").isNotNull();

    int free = ByteUtil.toByte(content[pos + 0x00]);
    bam.setFreeSectors(track, free);
    for (int i = 1, sector = 0; i < getBamEntrySize(); i++, sector += 8) {
      readBAM(bam, track, sector, content[pos + i]);
    }
  }

  /**
   * Read a bam byte.
   *
   * @param bam bam to fill
   * @param track current track
   * @param sector current sector
   * @param b byte to read
   */
  protected void readBAM(IBAM bam, int track, int sector, byte b) {
    requireThat(bam, "bam").isNotNull();

    int map = ByteUtil.toByte(b);
    for (int i = 0; i < 8 && sector < getSectors(track); i++, sector++) {
      bam.setFree(track, sector, (map & 0x01) != 0);
      map = map >> 1;
    }
  }

  @Override
  public IDirectory getDirectory() {
    byte[] name = new byte[16];
    System.arraycopy(getSector(18, 0), 0x90, name, 0, 16);
    byte[] idAndType = new byte[5];
    System.arraycopy(getSector(18, 0), 0xA2, idAndType, 0, 5);

    List<IFile> files = new ArrayList<>(18 * 8);
    for (SectorIterator iter = new SectorIterator(this, 18, 1); iter.hasNext(); ) {
      byte[] content = iter.next();
      for (int pos = 0; pos < 256; pos += 0x20) {
        files.add(getFile(content, pos));
      }
    }

    return new Directory(name, idAndType, files, getBAM().getFreeSectors());
  }

  /**
   * Read a directory entry.
   *
   * @param content sector buffer
   * @param pos position in sector buffer
   */
  protected IFile getFile(byte[] content, int pos) {
    // TODO evaluate all attributes
    FileMode mode = FileMode.fileMode(content[pos + 0x02]);
    int track = ByteUtil.toByte(content, pos + 0x03);
    int sector = ByteUtil.toByte(content, pos + 0x04);
    byte[] name = TextUtil.strip(content, pos + 0x05, 16);
    int sideTrack = ByteUtil.toByte(content, pos + 0x15); // rel only
    int sideSector = ByteUtil.toByte(content, pos + 0x16); // rel only
    int recordLength = ByteUtil.toByte(content, pos + 0x17); // rel only
    // 0x18-0x1D unused
    int size = ByteUtil.toWord(content, pos + 0x1E);

    return new File(mode, track, sector, name, size);
  }

  /**
   * Read file content.
   */
  public byte[] read(IFile file) {
    int track = file.getTrack();
    int sector = file.getSector();
    byte[] content = new byte[file.getSize() * 256];
    int pos = 0;
    while (track > 0) {
      byte[] sectorContent = getSector(track, sector);
      track = ByteUtil.toByte(sectorContent, 0);
      sector = ByteUtil.toByte(sectorContent, 1);
      if (track == 0) {
        System.arraycopy(sectorContent, 2, content, pos, sector);
        pos += sector;
      } else {
        System.arraycopy(sectorContent, 2, content, pos, 254);
        pos += 254;
      }
    }

    // Resize array to actual file size.
    byte[] result = new byte[pos];
    System.arraycopy(content, 0, result, 0, pos);
    return result;
  }
}

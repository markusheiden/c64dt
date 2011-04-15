package de.heiden.c64dt.disk;

import de.heiden.c64dt.util.ByteUtil;
import de.heiden.c64dt.util.TextUtil;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

import static de.heiden.c64dt.disk.SectorModelUtil.assertSector;

/**
 * Abstract disk image implementation.
 */
public abstract class AbstractDiskImage implements IDiskImage
{
  private int sides;
  private int tracks;
  private int tracksPerSide;
  private boolean hasErrors;
  private byte[][][] sectors;
  private Error[][] errors;

  /**
   * Constructor.
   *
   * @param sides number of disk sides
   * @param tracks number of tracks
   * @param hasErrors support error informations?
   */
  public AbstractDiskImage(int sides, int tracks, boolean hasErrors)
  {
    Assert.isTrue(sides >= 1 && sides <= 2, "Precondition: sides >= 1 && sides <= 2");
    Assert.isTrue(tracks >= 0, "Precondition: tracks >= 0");
    Assert.isTrue(tracks % sides == 0, "Precondition: tracks % sides == 0");

    this.sides = sides;
    this.tracks = tracks;
    this.tracksPerSide = tracks / sides;
    this.hasErrors = hasErrors;

    sectors = new byte[tracks][][];
    errors = new Error[tracks][];
    for (int track = 1; track <= tracks; track++)
    {
      int spt = getSectors(track);
      sectors[track - 1] = new byte[spt][];
      errors[track - 1] = new Error[spt];
      for (int sector = 0; sector < spt; sector++)
      {
        sectors[track - 1][sector] = new byte[256];
        errors[track - 1][sector] = Error.NO_ERROR;
      }
    }
  }

  //
  // ISectorModel
  //

  public int getSides()
  {
    return sides;
  }

  public int getTracks()
  {
    return tracks;
  }

  public int getTracksPerSide()
  {
    return tracksPerSide;
  }

  //
  //
  //

  public byte[] getSector(int track, int sector)
  {
    assertSector(this, track, sector);

    return sectors[track - 1][sector];
  }

  public void setSector(int track, int sector, byte[] content)
  {
    assertSector(this, track, sector);

    if (content.length != 256)
    {
      throw new IllegalArgumentException("Illegal sector content");
    }

    System.arraycopy(content, 0, sectors[track - 1][sector], 0, 256);
  }

  //
  // error support
  //

  public boolean hasErrors()
  {
    return hasErrors;
  }

  public boolean hasError(int track, int sector)
  {
    return hasErrors() && getError(track, sector).isError();
  }

  public Error getError(int track, int sector)
  {
    Assert.isTrue(hasErrors(), "Precondition: hasErrors()");
    assertSector(this, track, sector);

    return errors[track - 1][sector];
  }

  public void setError(int track, int sector, Error error)
  {
    Assert.isTrue(hasErrors(), "Precondition: hasErrors()");
    assertSector(this, track, sector);
    Assert.notNull(error, "Precondition: error != null");

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
  protected void readBAM(IBAM bam, int track, byte[] content, int pos)
  {
    Assert.notNull(bam, "Precondition: bam != null");
    Assert.notNull(content, "Precondition: content != null");

    int free = ByteUtil.toByte(content[pos + 0x00]);
    bam.setFreeSectors(track, free);
    for (int i = 1, sector = 0; i < getBamEntrySize(); i++, sector += 8)
    {
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
  protected void readBAM(IBAM bam, int track, int sector, byte b)
  {
    Assert.notNull(bam, "Precondition: bam != null");

    int map = ByteUtil.toByte(b);
    for (int i = 0; i < 8 && sector < getSectors(track); i++, sector++)
    {
      bam.setFree(track, sector, (map & 0x01) != 0);
      map = map >> 1;
    }
  }

  public IDirectory getDirectory()
  {
    byte[] name = new byte[16];
    System.arraycopy(getSector(18, 0), 0x90, name, 0, 16);
    byte[] idAndType = new byte[5];
    System.arraycopy(getSector(18, 0), 0xA2, idAndType, 0, 5);

    List<IFile> files = new ArrayList<IFile>(18 * 8);
    for (SectorIterator iter = new SectorIterator(this, 18, 1); iter.hasNext();)
    {
      byte[] content = iter.next();
      for (int pos = 0; pos < 256; pos += 0x20)
      {
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
  protected IFile getFile(byte[] content, int pos)
  {
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

}

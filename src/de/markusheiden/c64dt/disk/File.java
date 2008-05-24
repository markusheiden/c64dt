package de.markusheiden.c64dt.disk;

import de.markusheiden.c64dt.charset.C64Charset;
import de.markusheiden.c64dt.util.TextUtil;
import org.springframework.util.Assert;

/**
 * File implementation.
 */
public class File implements IFile {
  private FileMode mode;
  private int track;
  private int sector;
  private byte[] name;
  private int size;

  /**
   * Constructor.
   *
   * @param mode file mode
   * @param track start track
   * @param sector start sector
   * @param name name in C64 encoding
   * @param size file size in bytes
   */
  public File(FileMode mode, int track, int sector, byte[] name, int size) {
    Assert.notNull(mode, "Precondition: mode != null");
    Assert.isTrue(track >= 1, "Precondition: track >= 1");
    Assert.isTrue(sector >= 0, "Precondition: sector >= 0");
    Assert.notNull(name, "Precondition: name != null");
    Assert.isTrue(name.length <= 16, "Precondition: name.length <= 16");
    Assert.isTrue(size >= 0, "Precondition: size >= 0");

    this.mode = mode;
    this.track = track;
    this.sector = sector;
    this.name = TextUtil.strip(name);
    this.size = size;
  }

  public FileMode getMode() {
    Assert.notNull(mode, "Postcondition: result != null");
    return mode;
  }

  public int getTrack() {
    Assert.isTrue(track >= 1, "Postcondition: result >= 1");
    return track;
  }

  public int getSector() {
    Assert.isTrue(sector >= 0, "Postcondition: result >= 0");
    return sector;
  }

  public byte[] getName() {
    Assert.notNull(name, "Postcondition: result != null");
    Assert.isTrue(name.length <= 16, "Postcondition: result.length <= 16");
    return name;
  }

  public int getSize() {
    Assert.isTrue(size >= 0, "Postcondition: result >= 0");
    return size;
  }

  @Override
  public String toString() {
    return C64Charset.UPPER.toString(getName());
  }
}

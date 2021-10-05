package de.heiden.c64dt.disk;

import de.heiden.c64dt.charset.C64Charset;
import de.heiden.c64dt.charset.TextUtil;

import static de.heiden.c64dt.disk.Requirements.R;

/**
 * File implementation.
 */
public class File implements IFile {
  private final FileMode mode;
  private final int track;
  private final int sector;
  private final byte[] name;
  private final int size;

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
    R.requireThat(mode, "mode").isNotNull();
    if (!mode.isDeleted()) {
      R.requireThat(track, "track").isGreaterThanOrEqualTo(1);
      R.requireThat(sector, "sector").isGreaterThanOrEqualTo(0);
    }
    R.requireThat(name, "name").isNotNull();
    R.requireThat(name.length, "name.length").isLessThanOrEqualTo(16);
    R.requireThat(size, "size").isGreaterThanOrEqualTo(0);

    this.mode = mode;
    this.track = track;
    this.sector = sector;
    this.name = TextUtil.strip(name);
    this.size = size;
  }

  @Override
  public FileMode getMode() {
    R.requireThat(mode, "result").isNotNull();
    return mode;
  }

  @Override
  public int getTrack() {
    R.requireThat(track, "result").isGreaterThanOrEqualTo(1);
    return track;
  }

  @Override
  public int getSector() {
    R.requireThat(sector, "result").isGreaterThanOrEqualTo(0);
    return sector;
  }

  @Override
  public byte[] getName() {
    R.requireThat(name, "result").isNotNull();
    R.requireThat(name.length, "result.length").isLessThanOrEqualTo(16);
    return name;
  }

  @Override
  public int getSize() {
    R.requireThat(size, "result").isGreaterThanOrEqualTo(0);
    return size;
  }

  @Override
  public String toString() {
    return C64Charset.UPPER.toString(getName());
  }
}

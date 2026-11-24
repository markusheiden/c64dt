package de.heiden.c64dt.disk;

import de.heiden.c64dt.charset.C64Charset;
import de.heiden.c64dt.charset.TextUtil;

import static com.github.cowwoc.requirements10.java.DefaultJavaValidators.requireThat;

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
   * @param mode File mode.
   * @param track Start track.
   * @param sector Start sector.
   * @param name Name in C64 encoding.
   * @param size File size in bytes.
   */
  public File(FileMode mode, int track, int sector, byte[] name, int size) {
    if (!mode.isDeleted()) {
      requireThat(track, "track").isGreaterThanOrEqualTo(1);
      requireThat(sector, "sector").isGreaterThanOrEqualTo(0);
    }
    requireThat(name.length, "name.length").isLessThanOrEqualTo(16);
    requireThat(size, "size").isGreaterThanOrEqualTo(0);

    this.mode = mode;
    this.track = track;
    this.sector = sector;
    this.name = TextUtil.strip(name);
    this.size = size;
  }

  @Override
  public FileMode getMode() {
    return mode;
  }

  @Override
  public int getTrack() {
    requireThat(track, "result").isGreaterThanOrEqualTo(1);
    return track;
  }

  @Override
  public int getSector() {
    requireThat(sector, "result").isGreaterThanOrEqualTo(0);
    return sector;
  }

  @Override
  public byte[] getName() {
    requireThat(name.length, "result.length").isLessThanOrEqualTo(16);
    return name;
  }

  @Override
  public int getSize() {
    requireThat(size, "result").isGreaterThanOrEqualTo(0);
    return size;
  }

  @Override
  public String toString() {
    return C64Charset.UPPER.toString(getName());
  }
}

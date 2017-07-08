package de.heiden.c64dt.disk;

/**
 * Mode of a file.
 */
public class FileMode {
  private final FileType type;
  private final boolean replaced;
  private final boolean locked;
  private final boolean closed;

  /**
   * Get type.
   */
  public FileType getType() {
    return type;
  }

  /**
   * Is file being replaced?.
   */
  public boolean isReplaced() {
    return replaced;
  }

  /**
   * Is file locked?.
   * (< is displayed, when locked).
   */
  public boolean isLocked() {
    return locked;
  }

  /**
   * Is file closed?.
   * (* is displayed, when not correctly closed).
   */
  public boolean isClosed() {
    return closed;
  }

  /**
   * Should the file be displayed in the directory?.
   */
  public boolean isVisible() {
    return isClosed() || !getType().equals(FileType.DEL);
  }

  /**
   * Is the file deleted?.
   */
  public boolean isDeleted() {
    return getType().equals(FileType.DEL);
  }

  /**
   * Get mode for mode code.
   *
   * @param mode mode code
   */
  public static FileMode fileMode(int mode) {
    FileType type = FileType.fileType(mode & 0x0F);
    boolean replaced = (mode & (1 << 5)) != 0;
    boolean locked = (mode & (1 << 6)) != 0;
    boolean closed = (mode & (1 << 7)) != 0;

    return new FileMode(type, replaced, locked, closed);
  }

  /**
   * Convenience constructor with default values.
   *
   * @param type type
   */
  public FileMode(FileType type) {
    this(type, false, false, true);
  }

  /**
   * Constructor.
   *
   * @param type file type
   * @param replaced has the file been replaced?
   * @param locked is the file locked?
   * @param closed has the file correctly been closed?
   */
  public FileMode(FileType type, boolean replaced, boolean locked, boolean closed) {
    this.type = type;
    this.replaced = replaced;
    this.locked = locked;
    this.closed = closed;
  }
}

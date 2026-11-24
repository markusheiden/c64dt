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
   * (&lt; is displayed, when locked).
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
   * @param mode Mode code.
   */
  public static FileMode fileMode(int mode) {
    var type = FileType.fileType(mode & 0x0F);
    var replaced = (mode & (1 << 5)) != 0;
    var locked = (mode & (1 << 6)) != 0;
    var closed = (mode & (1 << 7)) != 0;

    return new FileMode(type, replaced, locked, closed);
  }

  /**
   * Convenience constructor with default values.
   *
   * @param type Type.
   */
  public FileMode(FileType type) {
    this(type, false, false, true);
  }

  /**
   * Constructor.
   *
   * @param type File type.
   * @param replaced Has the file been replaced?
   * @param locked Is the file locked?
   * @param closed Has the file correctly been closed?
   */
  public FileMode(FileType type, boolean replaced, boolean locked, boolean closed) {
    this.type = type;
    this.replaced = replaced;
    this.locked = locked;
    this.closed = closed;
  }
}

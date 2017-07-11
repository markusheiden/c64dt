package de.heiden.c64dt.disk;

import java.util.List;

import static org.bitbucket.cowwoc.requirements.core.Requirements.requireThat;

/**
 * Directory implementation.
 */
public class Directory implements IDirectory {
  private final byte[] name;
  private final byte[] idAndType;
  private final byte[] id;
  private final byte[] dosType;
  private final List<IFile> files;
  private final int freeBlocks;

  /**
   * Constructor.
   *
   * @param name name
   * @param idAndType id (2 bytes), 0xA0 and dos type (2 bytes)
   * @param files files
   */
  public Directory(byte[] name, byte[] idAndType, List<IFile> files, int freeBlocks) {
    requireThat(name, "name").isNotNull();
    requireThat(name.length, "name.length").isEqualTo(16);
    requireThat(idAndType, "idAndType").isNotNull();
    requireThat(idAndType.length, "idAndType.length").isEqualTo(5);
    requireThat(files, "files").isNotNull();
    requireThat(freeBlocks, "freeBlocks").isGreaterThanOrEqualTo(0);

    this.name = name;
    this.idAndType = idAndType;
    this.id = new byte[2];
    System.arraycopy(idAndType, 0x00, this.id, 0, 2);
    this.dosType = new byte[2];
    System.arraycopy(idAndType, 0x03, this.dosType, 0, 2);
    this.files = files;
    this.freeBlocks = freeBlocks;
  }

  @Override
  public byte[] getName() {
    requireThat(name, "name").isNotNull();
    requireThat(name.length, "name.length").isEqualTo(16);
    return name;
  }

  @Override
  public byte[] getIdAndType() {
    requireThat(idAndType, "idAndType").isNotNull();
    requireThat(idAndType.length, "idAndType.length").isEqualTo(5);
    return idAndType;
  }

  @Override
  public byte[] getId() {
    requireThat(id, "id").isNotNull();
    requireThat(id.length, "id.length").isEqualTo(2);
    return id;
  }

  @Override
  public byte[] getDosType() {
    requireThat(dosType, "id").isNotNull();
    requireThat(dosType.length, "dosType.length").isEqualTo(2);
    return dosType;
  }

  @Override
  public List<IFile> getFiles() {
    return files;
  }

  @Override
  public int getFreeBlocks() {
    return freeBlocks;
  }
}

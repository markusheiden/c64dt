package de.heiden.c64dt.disk;

import org.springframework.util.Assert;

import java.util.List;

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
    Assert.notNull(name, "Precondition: name != null");
    Assert.isTrue(name.length == 16, "Precondition: name.length == 16");
    Assert.notNull(idAndType, "Precondition: idAndType != null");
    Assert.isTrue(idAndType.length == 5, "Precondition: idAndType.length == 5");
    Assert.notNull(files, "Precondition: files != null");
    Assert.isTrue(freeBlocks >= 0, "Precondition: freeBlocks >= 0");

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
    Assert.notNull(name, "Postcondition: result != null");
    Assert.isTrue(name.length == 16, "Postcondition: name.length == 16");
    return name;
  }

  @Override
  public byte[] getIdAndType() {
    Assert.notNull(idAndType, "Postcondition: result != null");
    Assert.isTrue(idAndType.length == 5, "Postcondition: result.length == 5");
    return idAndType;
  }

  @Override
  public byte[] getId() {
    Assert.notNull(id, "Postcondition: result != null");
    Assert.isTrue(id.length == 2, "Postcondition: result.length == 2");
    return id;
  }

  @Override
  public byte[] getDosType() {
    Assert.notNull(dosType, "Postcondition: result != null");
    Assert.isTrue(dosType.length == 2, "Postcondition: result.length == 2");
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

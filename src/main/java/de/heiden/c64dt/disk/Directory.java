package main.java.de.heiden.c64dt.disk;

import org.springframework.util.Assert;

import java.util.List;

/**
 * Directory implementation.
 */
public class Directory implements IDirectory {
  private byte[] name;
  private byte[] idAndType;
  private byte[] id;
  private byte[] dosType;
  private List<IFile> files;
  private int freeBlocks;

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

  public byte[] getName() {
    Assert.notNull(name, "Postcondition: result != null");
    Assert.isTrue(name.length == 16, "Postcondition: name.length == 16");
    return name;
  }

  public byte[] getIdAndType() {
    Assert.notNull(idAndType, "Postcondition: result != null");
    Assert.isTrue(idAndType.length == 5, "Postcondition: result.length == 5");
    return idAndType;
  }

  public byte[] getId() {
    Assert.notNull(id, "Postcondition: result != null");
    Assert.isTrue(id.length == 2, "Postcondition: result.length == 2");
    return id;
  }

  public byte[] getDosType() {
    Assert.notNull(dosType, "Postcondition: result != null");
    Assert.isTrue(dosType.length == 2, "Postcondition: result.length == 2");
    return dosType;
  }

  public List<IFile> getFiles() {
    return files;
  }

  public int getFreeBlocks() {
    return freeBlocks;
  }
}

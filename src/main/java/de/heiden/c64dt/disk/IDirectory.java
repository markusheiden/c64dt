package de.heiden.c64dt.disk;

import java.util.List;

/**
 * Directory.
 */
public interface IDirectory
{
  /**
   * Disk name in C64 encoding.
   */
  public byte[] getName();

  /**
   * Disk id, 0xA0 and dos type in C64 encoding.
   */
  public byte[] getIdAndType();

  /**
   * Disk id in C64 encoding.
   */
  public byte[] getId();

  /**
   * Disk dos type in C64 encoding.
   */
  public byte[] getDosType();

  /**
   * Directory entries.
   */
  public List<IFile> getFiles();

  /**
   * Free blocks.
   */
  public int getFreeBlocks();
}

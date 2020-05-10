package de.heiden.c64dt.disk;

import java.util.List;

/**
 * Directory.
 */
public interface IDirectory {
  /**
   * Disk name in C64 encoding.
   */
  byte[] getName();

  /**
   * Disk id, 0xA0 and dos type in C64 encoding.
   */
  byte[] getIdAndType();

  /**
   * Disk id in C64 encoding.
   */
  byte[] getId();

  /**
   * Disk dos type in C64 encoding.
   */
  byte[] getDosType();

  /**
   * Directory entries.
   */
  List<IFile> getFiles();

  /**
   * Free blocks.
   */
  int getFreeBlocks();
}

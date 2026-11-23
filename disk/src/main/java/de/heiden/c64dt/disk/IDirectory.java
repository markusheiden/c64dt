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
   * Disk ID, 0xA0 and DOS type in C64 encoding.
   */
  byte[] getIdAndType();

  /**
   * Disk ID in C64 encoding.
   */
  byte[] getId();

  /**
   * Disk DOS type in C64 encoding.
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

package main.java.de.heiden.c64dt.net.drive.path;

import main.java.de.heiden.c64dt.net.drive.stream.IStream;

import java.io.FileNotFoundException;

/**
 * Net drive path.
 */
public interface IPath {
  /**
   * Parent path.
   */
  IPath getParent();

  /**
   * Change path.
   *
   * @param path c64 encoded relative path
   */
  IPath changePath(byte[] path) throws FileNotFoundException;

  /**
   * Get stream of a file.
   */
  IStream getFile(byte[] filename) throws FileNotFoundException;
}

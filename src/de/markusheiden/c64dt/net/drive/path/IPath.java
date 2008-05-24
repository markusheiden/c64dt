package de.markusheiden.c64dt.net.drive.path;

import de.markusheiden.c64dt.net.drive.stream.IStream;

import java.io.FileNotFoundException;

/**
 * Created by IntelliJ IDEA.
 * User: Markus
 * Date: 24.05.2008
 * Time: 10:23:11
 * To change this template use File | Settings | File Templates.
 */
public interface IPath {
  /**
   * Parent path.
   */
  IPath getParent();

  /**
   * Get stream of a file.
   */
  IStream getFile(byte[] filename) throws FileNotFoundException;

  /**
   * Change path.
   *
   * @param path c64 encoded relative path
   */
  IPath changePath(byte[] path) throws FileNotFoundException;
}

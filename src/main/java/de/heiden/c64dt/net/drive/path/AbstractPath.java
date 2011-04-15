package de.heiden.c64dt.net.drive.path;

import de.heiden.c64dt.disk.IDirectory;
import de.heiden.c64dt.net.drive.stream.DirectoryStream;
import de.heiden.c64dt.net.drive.stream.IStream;
import org.springframework.util.Assert;

import java.io.FileNotFoundException;
import java.util.Arrays;

import static de.heiden.c64dt.net.drive.DeviceEncoding.encode;

/**
 * Directory path.
 */
public abstract class AbstractPath implements IPath
{
  private static final byte SEPARATOR = encode('/');
  private static final byte[] PARENT_DIR_PATH = encode("..");
  private static final byte[] CURRENTT_DIR_PATH = encode(".");
  private static final byte[] DIRECTORY_NAME = encode("$");

  private IPath parent;

  /**
   * Constructor.
   *
   * @param parent parent path, null means there is no parent
   */
  protected AbstractPath(IPath parent)
  {
    // set this as parent, when no parent is specified to not leave root dir
    this.parent = parent == null ? this : parent;
  }

  public IPath getParent()
  {
    Assert.notNull(parent, "Postcondition: result != null");
    return parent;
  }

  public IStream getFile(byte[] filename) throws FileNotFoundException
  {
    Assert.notNull(filename, "Precondition: filename != null");

    if (Arrays.equals(filename, DIRECTORY_NAME))
    {
      return new DirectoryStream(doDirectory());
    }
    else
    {
      return doFile(filename);
    }
  }

  /**
   * Get directory of current path.
   */
  protected abstract IDirectory doDirectory();

  /**
   * Get directory of path.
   *
   * @param filename c64 encoded filename
   */
  protected abstract IStream doFile(byte[] filename) throws FileNotFoundException;

  public IPath changePath(byte[] path) throws FileNotFoundException
  {
    int separatorPos = indexOfSeparator(path);

    IPath result;
    if (separatorPos < 0)
    {
      // plain path
      result = doChangePath(path);
    }
    else
    {
      // separate path into head and tail
      byte[] head = new byte[separatorPos];
      System.arraycopy(path, 0, head, 0, head.length);
      byte[] tail = new byte[path.length - separatorPos - 1];
      System.arraycopy(path, separatorPos + 1, tail, 0, tail.length);

      if (Arrays.equals(head, CURRENTT_DIR_PATH))
      {
        result = changePath(tail);
      }
      else if (Arrays.equals(head, PARENT_DIR_PATH))
      {
        result = getParent().changePath(tail);
      }
      else
      {
        result = doChangePath(head).changePath(tail);
      }
    }

    Assert.notNull(result, "Postcondition: result != null");
    return result;
  }

  /**
   * Change plain path.
   *
   * @param path c64 encoded path without any path separators
   */
  protected abstract IPath doChangePath(byte[] path) throws FileNotFoundException;

  //
  // helper
  //

  /**
   * Index of first path separator char
   *
   * @param path c64 encoded path
   * @return index of first separator char or -1 if none
   */
  protected int indexOfSeparator(byte[] path)
  {
    for (int i = 0; i < path.length; i++)
    {
      if (path[i] == SEPARATOR)
      {
        return i;
      }
    }

    return -1;
  }
}

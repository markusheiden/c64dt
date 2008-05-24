package de.markusheiden.c64dt.net.drive.path;

import de.markusheiden.c64dt.charset.C64Charset;
import de.markusheiden.c64dt.disk.IDirectory;
import de.markusheiden.c64dt.net.drive.stream.IStream;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.io.FileNotFoundException;

/**
 * Directory path.
 */
public abstract class AbstractPath implements IPath {
  private static final byte separator = C64Charset.LOWER.toBytes("/")[0];
  private static final byte[] parentDirPath = C64Charset.LOWER.toBytes("..");
  private static final byte[] currenttDirPath = C64Charset.LOWER.toBytes(".");
  private static final byte[] directoryName = C64Charset.LOWER.toBytes("$");

  private IPath parent;

  /**
   * Constructor.
   *
   * @param parent parent path, null means there is no parent
   */
  protected AbstractPath(IPath parent) {
    // set this as parent, when no parent is specified to not leave root dir
    this.parent = parent == null? this : parent;
  }

  public IPath getParent() {
    Assert.notNull(parent, "Postcondition: result != null");
    return parent;
  }

  public IStream getFile(byte[] filename) throws FileNotFoundException {
    Assert.notNull(filename, "Precondition: filename != null");

    if (Arrays.equals(filename, directoryName)) {
      IDirectory directory = doDirectory();
      // TODO implement
      return null;
    } else {
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

  public IPath changePath(byte[] path) throws FileNotFoundException {
    int separatorPos = indexOfSeparator(path);

    IPath result;
    if (separatorPos < 0) {
      // plain path
      result = doChangePath(path);
    } else {
      // separate path into head and tail
      byte[] head = new byte[separatorPos];
      System.arraycopy(path, 0, head, 0, head.length);
      byte[] tail = new byte[path.length - separatorPos - 1];
      System.arraycopy(path, separatorPos + 1, tail, 0, tail.length);

      if (Arrays.equals(head, currenttDirPath)) {
        result = changePath(tail);
      } else if (Arrays.equals(head, parentDirPath)) {
        result = getParent().changePath(tail);
      } else {
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
  protected int indexOfSeparator(byte[] path) {
    for (int i = 0; i < path.length; i++) {
      if (path[i] == separator) {
        return i;
      }
    }

    return -1;
  }

  /**
   * Encodes a string to c64 encoding.
   *
   * @param decoded string
   */
  protected byte[] encode(String decoded) {
    return C64Charset.LOWER.toBytes(decoded);
  }

  /**
   * Decodes a c64 encoded string.
   *
   * @param encoded c64 encoded string
   */
  protected String decode(byte[] encoded) {
    return C64Charset.LOWER.toString(encoded);
  }
}

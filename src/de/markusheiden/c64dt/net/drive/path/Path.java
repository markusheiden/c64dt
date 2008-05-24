package de.markusheiden.c64dt.net.drive.path;

import de.markusheiden.c64dt.disk.Directory;
import de.markusheiden.c64dt.disk.FileMode;
import de.markusheiden.c64dt.disk.FileType;
import de.markusheiden.c64dt.disk.IDirectory;
import de.markusheiden.c64dt.disk.IFile;
import de.markusheiden.c64dt.net.drive.stream.FileStream;
import de.markusheiden.c64dt.net.drive.stream.IStream;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represent a directory.
 */
public class Path extends AbstractPath {
  private File directory;

  public Path(IPath parent, File directory) throws FileNotFoundException {
    super(parent);

    Assert.notNull(directory, "Precondition: directory != null");
    if (!directory.isDirectory()) {
      throw new FileNotFoundException(directory.getPath() + " is no directory");
    }

    this.directory = directory;
  }

  protected IDirectory doDirectory() {
    List<IFile> entries = new ArrayList<IFile>();
    File[] files = directory.listFiles();
    for (int i = 0; i < files.length; i++) {
      File file = files[i];
      int extensionPos = file.getName().lastIndexOf('.');
      String extension = extensionPos >= 0? file.getName().substring(extensionPos + 1) : "";
      FileMode mode = new FileMode(file.isDirectory()? FileType.DIR : FileType.fileType(extension));
      byte[] filename = encode(trimTo16(file.getName()));
      int size = file.isDirectory ()? 0 : (int) (file.length() + 253 / 254);
      entries.add(new de.markusheiden.c64dt.disk.File(mode, 0, 0, filename, size));
    }
    return new Directory(encode(trimTo16(directory.getName())), encode(""), entries, 0);
  }

  /**
   * Trim filename length to max. 16 characters.
   *
   * @param filename
   */
  protected String trimTo16(String filename) {
    if (filename.length() <= 16) {
      return filename;
    }

    return filename.substring(0, 16);
  }

  protected IStream doFile(byte[] filename) throws FileNotFoundException {
    String decodedFilename = decode(filename);
    // TODO implement wildcard search
    File file = new File(directory, decodedFilename);
    return new FileStream(file);
  }

  protected IPath doChangePath(byte[] path) throws FileNotFoundException {
    String decodedPath = decode(path);
    File newPath = new File(directory, decodedPath);

    if (!newPath.exists()) {
      throw new FileNotFoundException(newPath.getPath() + " not found");
    } else if (newPath.isFile()) {
      if (!newPath.getName().toLowerCase().endsWith("d64")) {
        throw new FileNotFoundException(newPath + " is no directory");
      }
      return new D64Path(this, newPath);
    } else {
      return new Path(this, newPath);
    }
  }
}

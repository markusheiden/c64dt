package main.java.de.heiden.c64dt.net.drive.path;

import main.java.de.heiden.c64dt.disk.Directory;
import main.java.de.heiden.c64dt.disk.FileMode;
import main.java.de.heiden.c64dt.disk.FileType;
import main.java.de.heiden.c64dt.disk.IDirectory;
import main.java.de.heiden.c64dt.disk.IFile;
import static main.java.de.heiden.c64dt.net.drive.DeviceEncoding.decode;
import static main.java.de.heiden.c64dt.net.drive.DeviceEncoding.encode;
import main.java.de.heiden.c64dt.net.drive.stream.FileStream;
import main.java.de.heiden.c64dt.net.drive.stream.IStream;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Path to a directory.
 */
public class Path extends AbstractPath {
  private static final byte SPACE = encode(' ');

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
      int size = file.isDirectory ()? 0 : (int) ((file.length() + 253) / 254);
      entries.add(new main.java.de.heiden.c64dt.disk.File(mode, 1, 0, filename, size));
    }
    byte[] trimmedName = new byte[16];
    Arrays.fill(trimmedName, (byte) SPACE);
    byte[] dirName = encode(trimTo16(directory.getName()));
    System.arraycopy(dirName, 0, trimmedName, 0, dirName.length);
    return new Directory(trimmedName, encode(">NET<"), entries, 0);
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

  protected IPath doChangePath(byte[] path) throws FileNotFoundException {
    String decodedPath = decode(path);
    // TODO implement wildcard search
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

  protected IStream doFile(byte[] filename) throws FileNotFoundException {
    String decodedFilename = decode(filename);
    // TODO implement wildcard search
    File file = new File(directory, decodedFilename);
    return new FileStream(file);
  }
}

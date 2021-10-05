package de.heiden.c64dt.net.drive.path;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.heiden.c64dt.disk.Directory;
import de.heiden.c64dt.disk.FileMode;
import de.heiden.c64dt.disk.FileType;
import de.heiden.c64dt.disk.IDirectory;
import de.heiden.c64dt.disk.IFile;
import de.heiden.c64dt.net.drive.stream.FileStream;
import de.heiden.c64dt.net.drive.stream.IStream;

import static de.heiden.c64dt.common.Requirements.R;
import static de.heiden.c64dt.net.drive.DeviceEncoding.decode;
import static de.heiden.c64dt.net.drive.DeviceEncoding.encode;

/**
 * Path to a directory.
 */
public class Path extends AbstractPath {
  private static final byte SPACE = encode(' ');

  private File directory;

  public Path(IPath parent, File directory) throws FileNotFoundException {
    super(parent);

    R.requireThat(directory, "directory").isNotNull();
    if (!directory.isDirectory()) {
      throw new FileNotFoundException(directory.getPath() + " is no directory");
    }

    this.directory = directory;
  }

  @Override
  protected IDirectory doDirectory() {
    List<IFile> entries = new ArrayList<>();
    File[] files = directory.listFiles();
    for (File file : files) {
      int extensionPos = file.getName().lastIndexOf('.');
      String extension = extensionPos >= 0 ? file.getName().substring(extensionPos + 1) : "";
      FileMode mode = new FileMode(file.isDirectory() ? FileType.DIR : FileType.fileType(extension));
      byte[] filename = encode(trimTo16(file.getName()));
      int size = file.isDirectory() ? 0 : (int) ((file.length() + 253) / 254);
      entries.add(new de.heiden.c64dt.disk.File(mode, 1, 0, filename, size));
    }
    byte[] trimmedName = new byte[16];
    Arrays.fill(trimmedName, SPACE);
    byte[] dirName = encode(trimTo16(directory.getName()));
    System.arraycopy(dirName, 0, trimmedName, 0, dirName.length);
    return new Directory(trimmedName, encode(">NET<"), entries, 0);
  }

  /**
   * Trim filename length to max. 16 characters.
   *
   * @param filename File name
   */
  protected String trimTo16(String filename) {
    if (filename.length() <= 16) {
      return filename;
    }

    return filename.substring(0, 16);
  }

  @Override
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

  @Override
  protected IStream doFile(byte[] filename) throws FileNotFoundException {
    String decodedFilename = decode(filename);
    // TODO implement wildcard search
    File file = new File(directory, decodedFilename);
    return new FileStream(file);
  }
}

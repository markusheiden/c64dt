package de.heiden.c64dt.net.drive.stream;

import org.springframework.util.Assert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Stream for a java.io.File.
 */
public class FileStream extends AbstractStream {
  private RandomAccessFile file;

  public FileStream(File file) throws FileNotFoundException {
    Assert.notNull(file, "Precondition: file != null");

    if (!file.isFile()) {
      throw new FileNotFoundException(file.getPath() + " is no file");
    }
    if (!file.exists()) {
      throw new FileNotFoundException(file.getPath() + " not found");
    }

    this.file = new RandomAccessFile(file, "rw");
  }

  public byte[] doRead(int length) throws IOException {
    file.seek(getPosition());
    byte[] result = new byte[length];
    int read = file.read(result);

    if (read < 0) {
      result = new byte[0];
    } else if (read < length) {
      byte[] trimmed = new byte[read];
      System.arraycopy(result, 0, trimmed, 0, read);
      result = trimmed;
    }

    Assert.notNull(result, "Postcondition: result != null");
    return result;
  }

  public void doWrite(byte[] data) throws IOException {
    file.seek(getPosition());
    file.write(data);
  }
}

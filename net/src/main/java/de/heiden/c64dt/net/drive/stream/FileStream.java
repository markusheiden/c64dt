package de.heiden.c64dt.net.drive.stream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import static de.heiden.c64dt.net.Requirements.R;

/**
 * Stream for a java.io.File.
 */
public class FileStream extends AbstractStream {
  private RandomAccessFile file;

  public FileStream(File file) throws FileNotFoundException {
    R.requireThat(file, "file").isNotNull();

    if (!file.isFile()) {
      throw new FileNotFoundException(file.getPath() + " is no file");
    }
    if (!file.exists()) {
      throw new FileNotFoundException(file.getPath() + " not found");
    }

    this.file = new RandomAccessFile(file, "rw");
  }

  @Override
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

    R.requireThat(result, "result").isNotNull();
    return result;
  }

  @Override
  public void doWrite(byte[] data) throws IOException {
    file.seek(getPosition());
    file.write(data);
  }
}

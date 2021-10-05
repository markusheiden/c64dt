package de.heiden.c64dt.disk;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.heiden.c64dt.disk.Requirements.R;

/**
 * Abstract base class for disk image readers.
 */
public abstract class AbstractDiskImageReader {
  /**
   * Logger.
   */
  private final Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * Read disk image from file.
   *
   * @param file file
   */
  public IDiskImage read(File file) throws IOException, WrongDiskImageFormatException {
    R.requireThat(file, "file").isNotNull();

    return read(FileUtils.readFileToByteArray(file));
  }

  /**
   * Read disk image from stream.
   * The stream will not be closed.
   *
   * @param stream stream
   */
  public IDiskImage read(InputStream stream) throws IOException, WrongDiskImageFormatException {
    R.requireThat(stream, "stream").isNotNull();

    return read(IOUtils.toByteArray(stream));
  }

  /**
   * Read disk image from byte array.
   *
   * @param data image data to read from
   */
  public abstract IDiskImage read(byte[] data) throws WrongDiskImageFormatException;

  /**
   * Read disk image from byte array.
   *
   * @param data image data to read from
   * @param diskImage disk image
   * @return diskImage for method chaining
   */
  protected IDiskImage read(byte[] data, IDiskImage diskImage) {
    R.requireThat(data, "data").isNotNull();
    R.requireThat(diskImage, "diskImage").isNotNull();

    int i = 0;

    // read sector contents
    byte[] content = new byte[256];
    for (int track = 1; track <= diskImage.getTracks(); track++) {
      logger.debug("Reading track {}", track);
      for (int sector = 0; sector < diskImage.getSectors(track); sector++) {
        logger.debug("Reading sector {} from {}", sector, Integer.toHexString(i));
        System.arraycopy(data, i, content, 0, content.length);
        diskImage.setSector(track, sector, content);
        i += content.length;
      }
    }

    // read errors
    if (diskImage.hasErrors()) {
      logger.debug("Reading errors");
      for (int track = 1; track <= diskImage.getTracks(); track++) {
        for (int sector = 0; sector < diskImage.getSectors(track); sector++) {
          diskImage.setError(track, sector, Error.error(data[i]));
          i++;
        }
      }
    }

    R.requireThat(i, "i").isEqualTo(data.length, "data.length");

    R.requireThat(diskImage, "diskImage").isNotNull();
    return diskImage;
  }
}

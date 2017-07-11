package de.heiden.c64dt.disk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.bitbucket.cowwoc.requirements.core.Requirements.requireThat;

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
    requireThat(file, "file").isNotNull();

    return read(FileCopyUtils.copyToByteArray(file));
  }

  /**
   * Read disk image from stream.
   * The stream will not be closed.
   *
   * @param stream stream
   */
  public IDiskImage read(InputStream stream) throws IOException, WrongDiskImageFormatException {
    requireThat(stream, "stream").isNotNull();

    return read(FileCopyUtils.copyToByteArray(stream));
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
    requireThat(data, "data").isNotNull();
    requireThat(diskImage, "diskImage").isNotNull();

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

    requireThat(i, "i").isEqualTo(data.length, "data.length");

    requireThat(diskImage, "diskImage").isNotNull();
    return diskImage;
  }
}

package de.heiden.c64dt.disk;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.cowwoc.requirements10.java.DefaultJavaValidators.requireThat;

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
   * @param file File.
   */
  public IDiskImage read(File file) throws IOException, WrongDiskImageFormatException {
    return read(FileUtils.readFileToByteArray(file));
  }

  /**
   * Read disk image from stream.
   * The stream will not be closed.
   *
   * @param stream Stream.
   */
  public IDiskImage read(InputStream stream) throws IOException, WrongDiskImageFormatException {
    return read(IOUtils.toByteArray(stream));
  }

  /**
   * Read disk image from byte array.
   *
   * @param data Image data to read from.
   */
  public abstract IDiskImage read(byte[] data) throws WrongDiskImageFormatException;

  /**
   * Read disk image from byte array.
   *
   * @param data Image data to read from.
   * @param diskImage Disk image.
   * @return diskImage for method chaining.
   */
  protected IDiskImage read(byte[] data, IDiskImage diskImage) {
    var i = 0;

    // Read sector contents.
    var content = new byte[256];
    for (var track = 1; track <= diskImage.getTracks(); track++) {
      logger.debug("Reading track {}", track);
      for (var sector = 0; sector < diskImage.getSectors(track); sector++) {
        logger.debug("Reading sector {} from {}", sector, Integer.toHexString(i));
        System.arraycopy(data, i, content, 0, content.length);
        diskImage.setSector(track, sector, content);
        i += content.length;
      }
    }

    // Read errors.
    if (diskImage.hasErrors()) {
      logger.debug("Reading errors");
      for (var track = 1; track <= diskImage.getTracks(); track++) {
        for (var sector = 0; sector < diskImage.getSectors(track); sector++) {
          diskImage.setError(track, sector, Error.error(data[i]));
          i++;
        }
      }
    }

    requireThat(i, "i").isEqualTo(data.length, "data.length");

    return diskImage;
  }
}

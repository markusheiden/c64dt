package de.heiden.c64dt.disk;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Abstract base class for disk image readers.
 */
public abstract class AbstractDiskImageReader {
  private final Log logger = LogFactory.getLog(getClass());

  /**
   * Read disk image from file.
   *
   * @param file file
   */
  public IDiskImage read(File file) throws IOException, WrongDiskImageFormatException {
    Assert.notNull(file, "Precondition: file != null");

    return read(FileCopyUtils.copyToByteArray(file));
  }

  /**
   * Read disk image from stream.
   * The stream will not be closed.
   *
   * @param stream stream
   */
  public IDiskImage read(InputStream stream) throws IOException, WrongDiskImageFormatException {
    Assert.notNull(stream, "Precondition: stream != null");

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
    Assert.notNull(data, "Precondition: data != null");
    Assert.notNull(diskImage, "Precondition: diskImage != null");

    int i = 0;

    // read sector contents
    byte[] content = new byte[256];
    for (int track = 1; track <= diskImage.getTracks(); track++) {
      logger.debug("Reading track " + track);
      for (int sector = 0; sector < diskImage.getSectors(track); sector++) {
        logger.debug("Reading sector " + sector + " from " + Integer.toHexString(i));
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

    Assert.isTrue(i == data.length, "Check: i == data.length");

    Assert.notNull(diskImage, "Postcondition: result != null");
    return diskImage;
  }
}

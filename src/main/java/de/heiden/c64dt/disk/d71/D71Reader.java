package de.heiden.c64dt.disk.d71;

import de.heiden.c64dt.disk.AbstractDiskImageReader;
import de.heiden.c64dt.disk.IDiskImage;
import de.heiden.c64dt.disk.WrongDiskImageFormatException;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

/**
 * Reads a D71 image from a file.
 */
public class D71Reader extends AbstractDiskImageReader {
  private final Logger logger = Logger.getLogger(getClass());

  public static final int SIZE_70_TRACKS_NO_ERRORS = 2 * 174848;
  public static final int SIZE_70_TRACKS_WITH_ERRORS = 2 * 175531;

  public IDiskImage read(byte[] data) throws WrongDiskImageFormatException {
    Assert.notNull(data, "Precondition: data != null");

    int tracks = 70;
    boolean hasErrors;
    switch (data.length) {
      case SIZE_70_TRACKS_NO_ERRORS: hasErrors = false; break;
      case SIZE_70_TRACKS_WITH_ERRORS: hasErrors = true; break;
      default: throw new WrongDiskImageFormatException(data.length);
    }

    logger.info("Detected 70 tracks image " + (hasErrors ? "with" : "without") + " error informations");

    return read(data, new D71(tracks, hasErrors));
  }
}

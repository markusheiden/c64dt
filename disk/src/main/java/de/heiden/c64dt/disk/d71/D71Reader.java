package de.heiden.c64dt.disk.d71;

import de.heiden.c64dt.disk.AbstractDiskImageReader;
import de.heiden.c64dt.disk.IDiskImage;
import de.heiden.c64dt.disk.WrongDiskImageFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.cowwoc.requirements10.java.DefaultJavaValidators.requireThat;

/**
 * Reads a D71 image from a file.
 */
public class D71Reader extends AbstractDiskImageReader {
  /**
   * Logger.
   */
  private final Logger logger = LoggerFactory.getLogger(getClass());

  public static final int SIZE_70_TRACKS_NO_ERRORS = 2 * 174848;
  public static final int SIZE_70_TRACKS_WITH_ERRORS = 2 * 175531;

  @Override
  public IDiskImage read(byte[] data) throws WrongDiskImageFormatException {
    requireThat(data, "data").isNotNull();

    int tracks = 70;
    boolean hasErrors;
    switch (data.length) {
      case SIZE_70_TRACKS_NO_ERRORS:
        hasErrors = false;
        break;
      case SIZE_70_TRACKS_WITH_ERRORS:
        hasErrors = true;
        break;
      default:
        throw new WrongDiskImageFormatException(data.length);
    }

    logger.info("Detected 70 tracks image {} error information", hasErrors ? "with" : "without");

    return read(data, new D71(tracks, hasErrors));
  }
}

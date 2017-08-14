package de.heiden.c64dt.disk.d64;

import de.heiden.c64dt.disk.AbstractDiskImageReader;
import de.heiden.c64dt.disk.IDiskImage;
import de.heiden.c64dt.disk.WrongDiskImageFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.bitbucket.cowwoc.requirements.core.Requirements.requireThat;

/**
 * Reads a D64 image from a file.
 */
public class D64Reader extends AbstractDiskImageReader {
  /**
   * Logger.
   */
  private final Logger logger = LoggerFactory.getLogger(getClass());

  public static final int SIZE_35_TRACKS_NO_ERRORS = 174848;
  public static final int SIZE_35_TRACKS_WITH_ERRORS = 175531;
  public static final int SIZE_40_TRACKS_NO_ERRORS = 196608;
  public static final int SIZE_40_TRACKS_WITH_ERRORS = 197376;

  @Override
  public IDiskImage read(byte[] data) throws WrongDiskImageFormatException {
    requireThat("data", data).isNotNull();

    int tracks;
    boolean hasErrors;
    switch (data.length) {
      case SIZE_35_TRACKS_NO_ERRORS:
        tracks = 35;
        hasErrors = false;
        break;
      case SIZE_35_TRACKS_WITH_ERRORS:
        tracks = 35;
        hasErrors = true;
        break;
      case SIZE_40_TRACKS_NO_ERRORS:
        tracks = 40;
        hasErrors = false;
        break;
      case SIZE_40_TRACKS_WITH_ERRORS:
        tracks = 40;
        hasErrors = true;
        break;
      default:
        throw new WrongDiskImageFormatException(data.length);
    }

    logger.info("Detected {} tracks image {} error information", tracks, hasErrors ? "with" : "without");

    return read(data, new D64(tracks, hasErrors));
  }
}

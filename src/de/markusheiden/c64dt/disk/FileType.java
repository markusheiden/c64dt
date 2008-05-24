package de.markusheiden.c64dt.disk;

import org.springframework.util.Assert;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.HashMap;

/**
 * Value type for file type.
 */
public enum FileType {
  /**
   * Deleted file.
   */
  DEL(0x00, "DEL"),

  /**
   * Sequential file.
   */
  SEQ(0x01, "SEQ"),

  /**
   * Program.
   */
  PRG(0x02, "PRG"),

  /**
   * User defined sequential file.
   */
  USR(0x03, "PRG"),

  /**
   * Relative file.
   */
  REL(0x04, "REL"),

  //
  // unknown values
  //

  /**
   * Directory.
   */
  DIR(-1, "DIR"),

  /**
   * D64 disk image.
   */
  D64(-1, "D64"),

  /**
   * Any file.
   */
  ANY(-1, "ANY"),

  /**
   * Undefined value, which is assigned to all not defined file type codes.
   */
  UNKNOWN(-1, "XXX");

  private static final Logger logger = Logger.getLogger(FileType.class);

  private static Map<Byte, FileType> types;
  private static Map<String, FileType> extensions;

  private byte code;
  private String extension;

  /**
   * Get type code.
   */
  public byte getCode() {
    return (byte) (code & 0x0F);
  }

  /**
   * Get type string description.
   */
  public String getExtension() {
    return extension;
  }

  /**
   * Get type for type code.
   *
   * @param code type code
   */
  public static FileType fileType(int code) {
    FileType result = types.get((byte) code);
    if (result == null) {
      logger.info("Unknown file type code " + Integer.toHexString(code));
      result = UNKNOWN;
    }

    Assert.notNull(result, "Postcondition: result != null");
    return result;
  }

  /**
   * Get type for extension.
   *
   * @param extension extension
   * @return matching file type or ANY for undefined extensions
   */
  public static FileType fileType(String extension) {
    Assert.notNull(extension, "Precondition: extension != null");

    FileType result = extensions.get(extension.toUpperCase());
    if (result == null || result.code < 0) {
      logger.debug("Unknown file extension " + extension);
      result = ANY;
    }

    Assert.notNull(result, "Postcondition: result != null");
    return result;
  }

  /**
   * Constructor.
   *
   * @param code type code, -1 if none defined
   */
  private FileType(int code, String extension) {
    Assert.notNull(extension, "Precondition: extension != null");
    Assert.isTrue(extension.length() == 3, "Precondition: extension.length() == 3");

    this.code = (byte) code;
    this.extension = extension;

    if (code >= 0) {
      FileType removed = types().put((byte) code, this);
      Assert.isNull(removed, "Postcondition: no doubled codes");
    }
    FileType removed = extensions().put(extension.toUpperCase(), this);
    Assert.isNull(removed, "Postcondition: no doubled extensions");
  }

  /**
   * Map with all known defined type instances.
   */
  private Map<Byte, FileType> types() {
    if (types == null) {
      types = new HashMap<Byte, FileType>();
    }

    return types;
  }

  /**
   * Map with all known type instances.
   */
  private Map<String, FileType> extensions() {
    if (extensions == null) {
      extensions = new HashMap<String, FileType>();
    }

    return extensions;
  }
}

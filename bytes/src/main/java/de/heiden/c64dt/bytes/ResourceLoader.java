package de.heiden.c64dt.bytes;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Util class to load resources as byte arrays.
 */
public class ResourceLoader {
  /**
   * Load content from a file.
   *
   * @param length of expected content.
   * @param filename Filename of content.
   * @throws IOException
   */
  public static int[] load(int length, String filename) throws IOException {
    var stream = ResourceLoader.class.getResourceAsStream(filename);
    var read = new byte[length];
    var size = stream.read(read);
    stream.close();

    if (size != length) {
      throw new IOException("ROM image '" + filename + "' has a wrong size");
    }

    var result = new int[length];
    for (var i = 0; i < result.length; i++) {
      result[i] = read[i] & 0xFF;
    }

    return result;
  }

  /**
   * Load content from a file.
   *
   * @param filename Filename of content.
   * @throws IOException
   */
  public static int[] load(String filename) throws IOException {
    var stream = new FileInputStream(filename);
    var read = new byte[65536];
    var size = stream.read(read);
    stream.close();

    var result = new int[size];
    for (var i = 0; i < result.length; i++) {
      result[i] = read[i] & 0xFF;
    }

    return result;
  }
}

package de.heiden.c64dt.bytes;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.bitbucket.cowwoc.requirements.core.Requirements.requireThat;

/**
 * Util class to load resources as byte arrays.
 */
public class ResourceLoader {
  /**
   * Load content from a file
   *
   * @param length of expected content
   * @param filename filename of content
   * @throws IOException
   */
  public static int[] load(int length, String filename) throws IOException {
    InputStream stream = ResourceLoader.class.getResourceAsStream(filename);
    byte[] read = new byte[length];
    int size = stream.read(read);
    stream.close();

    if (size != length) {
      throw new IOException("ROM image '" + filename + "' has a wrong size");
    }

    int[] result = new int[length];
    for (int i = 0; i < result.length; i++) {
      result[i] = read[i] & 0xFF;
    }

    requireThat("result", result).isNotNull();
    return result;
  }

  /**
   * Load content from a file
   *
   * @param filename filename of content
   * @throws IOException
   */
  public static int[] load(String filename) throws IOException {
    InputStream stream = new FileInputStream(filename);
    byte[] read = new byte[65536];
    int size = stream.read(read);
    stream.close();

    int[] result = new int[size];
    for (int i = 0; i < result.length; i++) {
      result[i] = read[i] & 0xFF;
    }

    requireThat("result", result).isNotNull();
    return result;
  }
}

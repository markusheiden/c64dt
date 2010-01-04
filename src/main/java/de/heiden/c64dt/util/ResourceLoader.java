package de.heiden.c64dt.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Util class to load resources as byte arrays.
 */
public class ResourceLoader
{
  /**
   * Load content from a file
   *
   * @param length of expected content
   * @param filename filename of content
   * @throws Exception
   */
  public static int[] load(int length, String filename) throws IOException
  {
    InputStream stream = ResourceLoader.class.getResourceAsStream(filename);
    byte[] read = new byte[length];
    int size = stream.read(read);
    stream.close();

    if (size != length)
    {
      throw new IOException("ROM image '" + filename + "' has a wrong size");
    }

    int[] result = new int[length];
    for (int i = 0; i < result.length; i++)
    {
      result[i] = read[i] & 0xFF;
    }

    assert result != null : "result != null";
    return result;
  }
}

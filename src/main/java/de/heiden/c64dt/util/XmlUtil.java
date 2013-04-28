package de.heiden.c64dt.util;

import org.springframework.util.FileCopyUtils;

import javax.xml.bind.JAXB;
import java.io.*;

/**
 * XML utils.
 */
public class XmlUtil {
  /**
   * Marshal an arbitrary object with JAXB.
   *
   * @param object Object
   * @param stream Output stream to write to
   */
  public static void marshal(Object object, OutputStream stream) throws Exception {
    JAXB.marshal(object, stream);
    stream.close();
  }

  /**
   * Unmarshal an arbitrary object with JAXB.
   *
   * @param stream Input stream to read from
   * @param clazz Class of object
   */
  public static <O> O unmarshal(InputStream stream, Class<O> clazz) throws Exception {
    return JAXB.unmarshal(stream, clazz);
  }

  /**
   * Print xml to System.out.
   *
   * @param xml XML
   */
  public static void println(byte[] xml) throws Exception {
    FileCopyUtils.copy(new InputStreamReader(new ByteArrayInputStream(xml), "utf8"), new PrintWriter(System.out));
    System.out.println();
    System.out.flush();
  }
}

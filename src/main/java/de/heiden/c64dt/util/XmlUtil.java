package de.heiden.c64dt.util;

import javax.xml.bind.JAXB;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * XML utils.
 */
public class XmlUtil {
  /**
   * Marshal an arbitrary object with JAXB.
   *
   * @param object Object
   * @param stream Output stream to write to
   * @return Xml representation, needs to be imported
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
}

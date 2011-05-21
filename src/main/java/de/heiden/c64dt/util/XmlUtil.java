package de.heiden.c64dt.util;

import org.w3c.dom.Document;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import java.io.OutputStream;

/**
 * XML utils.
 */
public class XmlUtil
{
  /**
   * Writes a DOM to a stream. Uses pretty printing.
   *
   * @param document DOM document
   * @param stream Stream
   */
  public static void toStream(Document document, OutputStream stream) throws Exception
  {
    DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
    DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
    LSSerializer writer = impl.createLSSerializer();
    writer.getDomConfig().setParameter("format-pretty-print", true);
    LSOutput output = impl.createLSOutput();
    output.setByteStream(stream);
    writer.write(document, output);
  }
}

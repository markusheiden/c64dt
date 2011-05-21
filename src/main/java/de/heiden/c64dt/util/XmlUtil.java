package de.heiden.c64dt.util;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import javax.xml.bind.JAXB;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import java.io.OutputStream;

/**
 * XML utils.
 */
public class XmlUtil
{
  /**
   * Marshal an arbitrary object with JAXB.
   *
   * @param object Object
   * @return Xml representation, needs to be imported
   */
  public static Node marshal(Object object) throws Exception
  {
    DOMResult result = new DOMResult();
    JAXB.marshal(object, result);
    return ((Document) result.getNode()).getDocumentElement();
  }

  /**
   * Unmarshal an arbitrary object with JAXB.
   *
   * @param node Xml representation of object
   * @param clazz Class of object
   */
  public static <O> O unmarshal(Node node, Class<O> clazz) throws Exception
  {
    return JAXB.unmarshal(new DOMSource(node), clazz);
  }

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

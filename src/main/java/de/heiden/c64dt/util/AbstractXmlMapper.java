package de.heiden.c64dt.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface for xml mappers.
 */
public abstract class AbstractXmlMapper<O>
{
  /**
   * Mapped clazz.
   */
  private final Class<O> clazz;

  /**
   * Constructor.
   *
   * @param clazz Mapped class
   */
  public AbstractXmlMapper(Class<O> clazz) throws Exception
  {
    this.clazz = clazz;
  }

  /**
   * Write the object to the stream.
   *
   * @param object Object
   * @param stream Stream
   */
  public void write(O object, OutputStream stream) throws Exception
  {
    Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    Node node = XmlUtil.marshal(object);
    document.appendChild(document.importNode(node, true));
    write(object, document, document.getDocumentElement());
    document.normalizeDocument();
    XmlUtil.toStream(document, stream);
  }

  /**
   * Add the object as the given element to the document.
   *
   * @param o object to serialize
   * @param document document
   * @param element Element
   */
  public abstract void write(O o, Document document, Element element) throws Exception;

  /**
   * Read an object from the stream.
   *
   * @param stream Stream with xml file to read
   */
  public O read(InputStream stream) throws Exception
  {
    Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream);
    document.normalize();
    O result = XmlUtil.unmarshal(document, clazz);
    return read(document.getDocumentElement(), result);
  }

  /**
   * Read an object from the element.
   *
   * @param element Element to read
   * @param object Unmarshalled, incomplete object
   */
  public abstract O read(Element element, O object) throws Exception;
}

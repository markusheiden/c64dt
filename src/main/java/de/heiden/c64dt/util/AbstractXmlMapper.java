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
public abstract class AbstractXmlMapper<O extends Object>
{
  /**
   * Element name for mapped objects.
   */
  private final String elementName;

  /**
   * Mapped clazz.
   */
  private final Class<O> clazz;

  /**
   * Constructor.
   *
   * @param elementName Element name for mapped objects
   * @param clazz Mapped class
   */
  public AbstractXmlMapper(String elementName, Class<O> clazz) throws Exception
  {
    this.elementName = elementName;
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
    Node node = XmlUtil.marshal(object);

    Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    document.appendChild(document.importNode(node, true));

    write(object, document, document.getDocumentElement());

    // debug
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

package de.heiden.c64dt.util;

import de.heiden.c64dt.assembler.Reassembler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXB;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Interface for xml mappers.
 */
public abstract class AbstractXmlMapper<O extends Object>
{
  private final String elementName;
  protected final DocumentBuilder builder;

  /**
   * Constructor.
   */
  public AbstractXmlMapper(String elementName) throws Exception
  {
    this.elementName = elementName;

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    builder = factory.newDocumentBuilder();
  }

  /**
   * Write object to an stream.
   *
   * @param object Object
   * @param stream Stream
   */
  public void write(O object, OutputStream stream) throws Exception
  {
      Document document = builder.newDocument();
      Element reassemblerElement = document.createElement(elementName);
      document.appendChild(reassemblerElement);
      write(object, document, reassemblerElement);

      // debug
      XmlUtil.toStream(document, stream);
  }

  /**
   * Read object from stream.
   *
   * @param stream with xml file to read
   * @return Reassembler
   */
  public O read(InputStream stream) throws Exception
  {
    Document document = builder.parse(stream);
    Element element = document.getDocumentElement();
    return read(element);
  }

  /**
   * Add an object as the given element to the document.
   *
   * @param o object to serialize
   * @param document document
   * @param element  Element
   */
  public abstract void write(O o, Document document, Element element) throws Exception;


  /**
   * Read an object from the element.
   *
   * @param element Element to read
   * @return Deserialized object
   */
  public abstract O read(Element element) throws Exception;

  /**
   * Marshal an arbitrary object with JAXB.
   *
   * @param object Object
   * @param document Document to create the node for
   * @return Xml representation
   */
  protected Node marshal(Object object, Document document) throws Exception
  {
    DOMResult result = new DOMResult();
    JAXB.marshal(object, result);
    Document elementDocument = (Document) result.getNode();
    return document.importNode(elementDocument.getDocumentElement(), true);
  }

  /**
   * Unmarshal an arbitrary object with JAXB.
   *
   * @param element Xml representation of object
   * @param clazz Class of object
   */
  protected <O> O unmarshal(Element element, Class<O> clazz) throws Exception
  {
    return JAXB.unmarshal(new DOMSource(element), clazz);
  }
}

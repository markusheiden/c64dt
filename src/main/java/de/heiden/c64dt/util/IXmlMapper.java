package de.heiden.c64dt.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;

/**
 * Interface for xml mappers.
 */
public interface IXmlMapper<O extends Object>
{
  /**
   * Add command buffer to document.
   *
   * @param o object to serialize
   * @param document document
   * @param commandsElement element for command buffer
   */
  public void write(O o, Document document, Element commandsElement) throws IOException;


  /**
   * Read command buffer from document.
   *
   * @param commandsElement element to read
   * @return deserialized object
   */
  public O read(Element commandsElement) throws IOException;

}

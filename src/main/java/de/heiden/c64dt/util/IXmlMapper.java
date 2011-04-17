package de.heiden.c64dt.util;

import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.assembler.command.CommandBuffer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.SortedMap;

import static de.heiden.c64dt.util.HexUtil.hexBytePlain;
import static de.heiden.c64dt.util.HexUtil.hexWordPlain;

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

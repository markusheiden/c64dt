package de.heiden.c64dt.assembler.command;

import de.heiden.c64dt.assembler.CodeType;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Map.Entry;
import java.util.SortedMap;

import static de.heiden.c64dt.util.HexUtil.*;

/**
 * XML-Mapper to read and write the reassembler model.
 */
public class CommandBufferMapper
{
  /**
   * Add command buffer to document.
   *
   * @param commands command buffer
   * @param document document
   * @param parent parent element of command buffer
   */
  public void write(CommandBuffer commands, Document document, Element parent)
  {
    Element commandsElement = document.createElement("commands");
    parent.appendChild(commandsElement);

    // code
    Element codeElement = document.createElement("code");
    commandsElement.appendChild(codeElement);

    byte[] code = commands.getCode();
    StringBuilder codeHex = new StringBuilder(code.length * 2);
    for (byte codeByte : code)
    {
      codeHex.append(hexBytePlain(codeByte));
    }
    codeElement.setTextContent(codeHex.toString());
    
    // start addresses
    Element addressesElement = document.createElement("addresses");
    commandsElement.appendChild(addressesElement);

    SortedMap<Integer,Integer> startAddresses = commands.getStartAddresses();
    for (Entry<Integer, Integer> entry : startAddresses.entrySet())
    {
      Element addressElement = document.createElement("address");
      addressElement.setAttribute("index", hexWordPlain(entry.getKey()));
      addressElement.setAttribute("type", hexWordPlain(entry.getValue()));
      addressesElement.appendChild(addressElement);
    }

    // detected code types
    Element typesElement = document.createElement("types");
    commandsElement.appendChild(typesElement);

    for (int index = 0; index < commands.getLength();)
    {
      int startIndex = index;
      CodeType type = commands.getType(index++);
      if (!type.isUnknown()) {
        // count indexes with the same type
        int count = 1;
        for (; index < commands.getLength(); index++, count++)
        {
          if (!commands.getType(index).equals(type)) {
            break;
          }
        }

        Element typeElement = document.createElement("type");
        typeElement.setAttribute("index", hexWordPlain(startIndex));
        if (count > 1) {
          typeElement.setAttribute("end", hexWordPlain(index));

        }
        typeElement.setAttribute("type", type.name());
        typesElement.appendChild(typeElement);
      }
    }
  }
}

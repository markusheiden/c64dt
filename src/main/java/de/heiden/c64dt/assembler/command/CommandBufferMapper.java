package de.heiden.c64dt.assembler.command;

import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.util.ByteUtil;
import de.heiden.c64dt.util.IXmlMapper;
import org.springframework.jca.cci.core.InteractionCallback;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Map.Entry;
import java.util.SortedMap;

import static de.heiden.c64dt.util.HexUtil.*;

/**
 * XML-Mapper to read and write the reassembler model.
 */
public class CommandBufferMapper implements IXmlMapper<CommandBuffer>
{
  /**
   * Add command buffer to document.
   *
   * @param commands command buffer
   * @param document document
   * @param commandsElement element for command buffer
   */
  public void write(CommandBuffer commands, Document document, Element commandsElement)
  {

    // code
    Element codeElement = document.createElement("code");
    commandsElement.appendChild(codeElement);

    byte[] code = commands.getCode();
    StringBuilder codeHex = new StringBuilder(code.length * 2);
    for (int i = 0; i < code.length; i++)
    {
      codeHex.append(hexBytePlain(ByteUtil.toByte(code, i)));
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
      addressElement.setAttribute("base", hexWordPlain(entry.getValue()));
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


  /**
   * Read command buffer from document.
   *
   * @param commandsElement element to read
   */
  public CommandBuffer read(Element commandsElement) {
    // code
    Node codeElement = commandsElement.getElementsByTagName("code").item(0);
    String codeData = codeElement.getTextContent().trim();
    byte[] code = new byte[codeData.length() / 2];
    for (int i = 0; i < codeData.length(); i += 2)
    {
      code[i / 2] = (byte) Integer.parseInt(codeData.substring(i, i + 2), 16);
    }

    // start address
    Node addressesElement = commandsElement.getElementsByTagName("addresses").item(0);
    NodeList addressElements = addressesElement.getChildNodes();
    Element startAddressElement = (Element) addressElements.item(0);
    int startAddress = Integer.parseInt(startAddressElement.getAttribute("base"), 16);
    CommandBuffer commands = new CommandBuffer(code, startAddress);

    // remaining addresses
    for (int i = 1; i < addressElements.getLength() - 1; i++)
    {
      Element addressElement = (Element) addressElements.item(i);
      int index = Integer.parseInt(addressElement.getAttribute("index"), 16);
      int address = Integer.parseInt(addressElement.getAttribute("base"), 16);
      commands.rebase(index, address);
    }

    // detected code types
    Element typesElement = (Element) commandsElement.getElementsByTagName("types").item(0);
    NodeList typeElements = typesElement.getElementsByTagName("type");
    for (int i = 0; i < typeElements.getLength(); i++)
    {
      Element typeElement = (Element) typeElements.item(i);
      int index = Integer.parseInt(typeElement.getAttribute("index"), 16);
      CodeType type = CodeType.valueOf(typeElement.getAttribute("type"));
      if (typeElement.hasAttribute("end")) {
        int end = Integer.parseInt(typeElement.getAttribute("end"), 16);
        commands.setType(index, end, type);
      } else {
        commands.setType(index, type);
      }
    }

    return commands;
  }
}
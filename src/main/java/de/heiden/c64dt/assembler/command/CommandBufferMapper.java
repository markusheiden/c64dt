package de.heiden.c64dt.assembler.command;

import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.util.AbstractXmlMapper;
import de.heiden.c64dt.util.ByteUtil;
import org.springframework.util.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Map.Entry;
import java.util.SortedMap;

import static de.heiden.c64dt.util.HexUtil.hexBytePlain;
import static de.heiden.c64dt.util.HexUtil.hexPlain;
import static de.heiden.c64dt.util.HexUtil.hexWordPlain;
import static de.heiden.c64dt.util.HexUtil.parseHexWordPlain;

/**
 * XML-Mapper to read and write the reassembler model.
 */
public class CommandBufferMapper extends AbstractXmlMapper<CommandBuffer>
{
  /**
   * Constructor.
   */
  public CommandBufferMapper() throws Exception
  {
    super(CommandBuffer.class);
  }

  @Override
  public void write(CommandBuffer commands, Document document, Element commandsElement)
  {
    // code
    Element codeElement = document.createElement("code");
    commandsElement.appendChild(codeElement);

    byte[] code = commands.getCode();
    StringBuilder codeHex = new StringBuilder(code.length * 2);
    for (int i = 0; i < code.length; i++)
    {
      codeHex.append(hexBytePlain(ByteUtil.toByte(code[i])));
    }
    codeElement.setTextContent(codeHex.toString());

    // start addresses
    Element addressesElement = document.createElement("addresses");
    commandsElement.appendChild(addressesElement);

    SortedMap<Integer, Integer> startAddresses = commands.getStartAddresses();
    for (Entry<Integer, Integer> entry : startAddresses.entrySet())
    {
      Element addressElement = document.createElement("address");
      addressElement.setAttribute("index", hexWordPlain(entry.getKey()));
      addressElement.setAttribute("base", hexWordPlain(entry.getValue()));
      addressesElement.appendChild(addressElement);
    }

    // subroutines
    Element subroutinesElement = document.createElement("subroutines");
    commandsElement.appendChild(subroutinesElement);

    for (Entry<Integer, Subroutine> entry : commands.getSubroutines().entrySet())
    {
      Element subroutineElement = document.createElement("subroutine");
      subroutineElement.setAttribute("address", hexWordPlain(entry.getKey()));
      subroutineElement.setAttribute("arguments", hexPlain(entry.getValue().getArguments()));
      subroutineElement.setTextContent(entry.getValue().getType().toString());
      subroutinesElement.appendChild(subroutineElement);
    }

    // detected code types
    Element typesElement = document.createElement("types");
    commandsElement.appendChild(typesElement);

    for (int index = 0; index < commands.getLength(); )
    {
      int startIndex = index;
      CodeType type = commands.getType(index++);
      if (type.isUnknown())
      {
        continue;
      }

      // count indexes with the same type
      int count = 1;
      for (; index < commands.getLength(); index++, count++)
      {
        if (!commands.getType(index).equals(type))
        {
          break;
        }
      }

      Element typeElement = document.createElement("type");
      typeElement.setAttribute("index", hexWordPlain(startIndex));
      if (count > 1)
      {
        typeElement.setAttribute("end", hexWordPlain(index));
      }
      typeElement.setTextContent(type.name());
      typesElement.appendChild(typeElement);
    }
  }

  @Override
  public CommandBuffer read(Element commandsElement, CommandBuffer dummy)
  {
    // code
    Node codeElement = commandsElement.getElementsByTagName("code").item(0);
    String codeData = codeElement.getTextContent().trim();
    byte[] code = new byte[codeData.length() / 2];
    for (int i = 0; i < codeData.length(); i += 2)
    {
      code[i / 2] = (byte) parseHexWordPlain(codeData.substring(i, i + 2));
    }

    // base addresses
    Element addressesElement = (Element) commandsElement.getElementsByTagName("addresses").item(0);
    NodeList addressElements = addressesElement.getElementsByTagName("address");

    // start address / first base address
    Element startAddressElement = (Element) addressElements.item(0);
    int startIndex = parseHexWordPlain(startAddressElement.getAttribute("index"));
    Assert.isTrue(startIndex == 0, "Check: startIndex == 0");

    // the start address is the first base address and automatically sets the end base address
    int startAddress = parseHexWordPlain(startAddressElement.getAttribute("base"));
    CommandBuffer commands = new CommandBuffer(code, startAddress);

    // remaining base addresses
    for (int i = 1; i < addressElements.getLength() - 1; i++)
    {
      Element addressElement = (Element) addressElements.item(i);
      int index = parseHexWordPlain(addressElement.getAttribute("index"));
      int address = parseHexWordPlain(addressElement.getAttribute("base"));
      commands.rebase(index, address);
    }

    // end base address
    Element endAddressElement = (Element) addressElements.item(addressElements.getLength() - 1);
    int endIndex = parseHexWordPlain(endAddressElement.getAttribute("index"));
    Assert.isTrue(endIndex == code.length, "Check: endIndex == code.length");
    int endAddress = parseHexWordPlain(endAddressElement.getAttribute("base"));
    Assert.isTrue(endAddress == startAddress, "Check: endAddress == startAddress");

    // subroutines
    Element subroutinesElement = (Element) commandsElement.getElementsByTagName("subroutines").item(0);
    NodeList subroutineElements = subroutinesElement.getElementsByTagName("subroutine");
    for (int i = 0; i < subroutineElements.getLength(); i++)
    {
      Element subroutineElement = (Element) subroutineElements.item(i);
      int address = parseHexWordPlain(subroutineElement.getAttribute("address"));
      int arguments = parseHexWordPlain(subroutineElement.getAttribute("arguments"));
      CodeType type = CodeType.valueOf(subroutineElement.getTextContent().trim());
      commands.addSubroutine(new Subroutine(address, arguments, type));
    }

    // detected code types
    Element typesElement = (Element) commandsElement.getElementsByTagName("types").item(0);
    NodeList typeElements = typesElement.getElementsByTagName("type");
    for (int i = 0; i < typeElements.getLength(); i++)
    {
      Element typeElement = (Element) typeElements.item(i);
      int index = parseHexWordPlain(typeElement.getAttribute("index"));
      CodeType type = CodeType.valueOf(typeElement.getTextContent().trim());
      if (typeElement.hasAttribute("end"))
      {
        int end = parseHexWordPlain(typeElement.getAttribute("end"));
        commands.setType(index, end, type);
      }
      else
      {
        commands.setType(index, type);
      }
    }

    return commands;
  }
}

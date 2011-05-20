package de.heiden.c64dt.assembler;

import de.heiden.c64dt.assembler.command.CommandBuffer;
import de.heiden.c64dt.assembler.command.CommandBufferMapper;
import de.heiden.c64dt.assembler.detector.IDetector;
import de.heiden.c64dt.util.AbstractXmlMapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;

/**
 * XML-Mapper to read and write the reassembler model.
 */
public class ReassemblerMapper extends AbstractXmlMapper<Reassembler>
{
  /**
   * Constructor.
   */
  public ReassemblerMapper() throws Exception
  {
    super("reassembler");
  }

  @Override
  public void write(Reassembler reassembler, Document document, Element reassemblerElement) throws Exception
  {
    Element detectorsElement = document.createElement("detectors");
    reassemblerElement.appendChild(detectorsElement);

    List<IDetector> detectors = reassembler.getDetectors();
    for (IDetector detector : detectors)
    {
      detectorsElement.appendChild(marshal(detector, document));
    }

    Element commandsElement = document.createElement("commands");
    reassemblerElement.appendChild(commandsElement);

    new CommandBufferMapper().write(reassembler.getCommands(), document, commandsElement);
  }

  @Override
  public Reassembler read(Element reassemblerElement) throws Exception
  {
    Reassembler reassembler = new Reassembler();

    Element detectorsElement = (Element) reassemblerElement.getElementsByTagName("detectors").item(0);
    NodeList detectorElements = detectorsElement.getChildNodes();
    for (int i = 0; i < detectorElements.getLength(); i++)
    {
      Node node = detectorElements.item(i);
      if (node instanceof Element)
      {
        reassembler.add(unmarshal((Element) node, IDetector.class));
      }
    }

    Element commandsElement = (Element) reassemblerElement.getElementsByTagName("commands").item(0);
    CommandBuffer commands = new CommandBufferMapper().read(commandsElement);
    reassembler.reassemble(commands);

    return reassembler;
  }
}

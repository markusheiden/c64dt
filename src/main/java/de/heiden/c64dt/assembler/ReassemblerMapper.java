package de.heiden.c64dt.assembler;

import de.heiden.c64dt.assembler.command.CommandBuffer;
import de.heiden.c64dt.assembler.command.CommandBufferMapper;
import de.heiden.c64dt.assembler.detector.IDetector;
import de.heiden.c64dt.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.PrintWriter;
import java.util.List;

import static de.heiden.c64dt.util.HexUtil.hexWordPlain;

/**
 * XML-Mapper to read and write the reassembler model.
 */
public class ReassemblerMapper
{
  public void write(Reassembler reassembler) {
    try
    {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();

      Document document = builder.newDocument();
      write(reassembler, document);

      // debug
      XmlUtil.toStream(document, System.out);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Write reassembler to document.
   *
   * @param reassembler Reassembler
   * @param document Document
   */
  public void write(Reassembler reassembler, Document document) throws Exception
  {
    Element reassemblerElement = document.createElement("reassembler");
    document.appendChild(reassemblerElement);

    Element detectorsElement = document.createElement("detectors");
    reassemblerElement.appendChild(detectorsElement);

    List<IDetector> detectors = reassembler.getDetectors();
    for (IDetector detector : detectors)
    {
      Element detectorElement = document.createElement("detector");
      detectorElement.setAttribute("class", detector.getClass().getName());
      detectorsElement.appendChild(detectorElement);
    }

    Element commandsElement = document.createElement("commands");
    reassemblerElement.appendChild(commandsElement);

    new CommandBufferMapper().write(reassembler.getCommands(), document, commandsElement);
  }

  /**
   * Read reassembler from document.
   *
   * @param document Document
   */
  public Reassembler read(Document document) throws Exception {
    Reassembler reassembler = new Reassembler();

    Element reassemblerElement = (Element) document.getElementsByTagName("reassembler").item(0);

    Element commandsElement = (Element) reassemblerElement.getElementsByTagName("commands").item(0);
    CommandBuffer commands = new CommandBufferMapper().read(commandsElement);
    reassembler.reassemble(commands);

    Element detectorsElement = (Element) reassemblerElement.getElementsByTagName("detectors").item(0);
    NodeList detectorElements = detectorsElement.getElementsByTagName("detector");
    for (int i = 0; i < detectorElements.getLength(); i++)
    {
      Element detectorElement = (Element) detectorElements.item(i);
      String clazz = detectorElement.getAttribute("class");
      reassembler.add((IDetector) Class.forName(clazz).newInstance());
    }

    return reassembler;
  }
}

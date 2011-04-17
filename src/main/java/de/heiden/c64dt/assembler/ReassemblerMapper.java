package de.heiden.c64dt.assembler;

import de.heiden.c64dt.assembler.command.CommandBufferMapper;
import de.heiden.c64dt.assembler.detector.IDetector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
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

      // Prepare the DOM document for writing
      Source source = new DOMSource(document);

      // Prepare the output file
      Result result = new StreamResult(System.out);

      // Write the DOM document to the file
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.transform(source, result);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  private void write(Reassembler reassembler, Document document) throws Exception
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

    new CommandBufferMapper().write(reassembler.getCommands(), document, reassemblerElement);

  }
}

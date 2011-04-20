package de.heiden.c64dt.util;

import org.w3c.dom.Document;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;

/**
 * XML utils.
 */
public class XmlUtil
{
  public static void toStream(Document document, OutputStream os) throws Exception
  {
    // Prepare the DOM document for writing
    Source source = new DOMSource(document);

    // Prepare the output file
    Result result = new StreamResult(os);

    // Write the DOM document to the file
    Transformer transformer = TransformerFactory.newInstance().newTransformer();
    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.transform(source, result);
  }
}

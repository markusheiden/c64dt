package de.heiden.c64dt.assembler;

import de.heiden.c64dt.assembler.command.CommandBuffer;
import de.heiden.c64dt.assembler.command.CommandBufferMapper;
import de.heiden.c64dt.util.AbstractXmlMapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * XML-Mapper to read and write the reassembler model.
 */
public class ReassemblerMapper extends AbstractXmlMapper<Reassembler>
{
  private final CommandBufferMapper commandBufferMapper = new CommandBufferMapper();

  /**
   * Constructor.
   */
  public ReassemblerMapper() throws Exception
  {
    super("reassembler", Reassembler.class);
  }

  @Override
  public void write(Reassembler reassembler, Document document, Element reassemblerElement) throws Exception
  {
    // nothing to do for reassembler, because JAXB handles it completely

    // post process commands, because JAXB just handles them partly
    Element commandsElement = (Element) reassemblerElement.getElementsByTagName("commands").item(0);
    commandBufferMapper.write(reassembler.getCommands(), document, commandsElement);
  }

  @Override
  public Reassembler read(Element reassemblerElement, Reassembler reassembler) throws Exception
  {
    // nothing to do for reassembler, because JAXB handles it completely

    // post process commands, because JAXB just handles them partly
    Element commandsElement = (Element) reassemblerElement.getElementsByTagName("commands").item(0);
    CommandBuffer commands = commandBufferMapper.read(commandsElement, reassembler.getCommands());
    reassembler.reassemble(commands);

    return reassembler;
  }
}

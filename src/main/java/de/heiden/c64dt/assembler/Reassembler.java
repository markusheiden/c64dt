package de.heiden.c64dt.assembler;

import de.heiden.c64dt.assembler.command.CommandBuffer;
import de.heiden.c64dt.assembler.command.CommandBufferMapper;
import de.heiden.c64dt.assembler.command.CommandIterator;
import de.heiden.c64dt.assembler.command.ICommand;
import de.heiden.c64dt.assembler.detector.*;
import de.heiden.c64dt.assembler.label.ExternalLabel;
import de.heiden.c64dt.assembler.label.ILabel;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import static de.heiden.c64dt.util.ByteUtil.toWord;
import static de.heiden.c64dt.util.HexUtil.*;

/**
 * Reassembler.
 */
@XmlRootElement(name = "reassembler")
public class Reassembler {
  /**
   * Logger.
   */
  private final Logger logger = Logger.getLogger(getClass());

  /**
   * Code type detectors to use.
   */
  @XmlElementWrapper(name = "detectors")
  @XmlElements({
    @XmlElement(name = "bit", type = BitDetector.class),
    @XmlElement(name = "brk", type = BrkDetector.class),
    @XmlElement(name = "jsr", type = JsrDetector.class),
    @XmlElement(name = "label", type = LabelDetector.class),
    @XmlElement(name = "reachability", type = Reachability.class),
  })
  private final List<IDetector> detectors = new ArrayList<IDetector>();

  /**
   * Reassembled code.
   */
  @XmlElement(name = "commands")
  @XmlJavaTypeAdapter(CommandBufferMapper.class)
  private CommandBuffer commands;

  /**
   * Constructor.
   */
  public Reassembler() {
    // add default detectors
    detectors.add(new Reachability());
    detectors.add(new LabelDetector());
    detectors.add(new BrkDetector());
    detectors.add(new BitDetector());
    detectors.add(new JsrDetector());

    commands = new CommandBuffer(new byte[0], 0);
  }

  /**
   * Command buffer.
   */
  public CommandBuffer getCommands() {
    return commands;
  }

  /**
   * Add code type detector to use.
   *
   * @param detector code type detector
   */
  public void add(IDetector detector) {
    Assert.notNull(detector, "Precondition: detector != null");

    detectors.add(detector);
  }

  /**
   * All detectors.
   * Just for the mapper.
   */
  List<IDetector> getDetectors() {
    return detectors;
  }

  /**
   * Reassemble program.
   *
   * @param input program with start address
   */
  public void reassemble(InputStream input) throws IOException {
    Assert.notNull(input, "Precondition: input != null");

    reassemble(FileCopyUtils.copyToByteArray(input));
  }

  /**
   * Reassemble program.
   *
   * @param program program with start address
   */
  public void reassemble(byte[] program) throws IOException {
    Assert.notNull(program, "Precondition: program != null");
    Assert.isTrue(program.length >= 2, "Precondition: program.length >= 2");

    int address = toWord(program, 0);
    byte[] code = new byte[program.length - 2];
    System.arraycopy(program, 2, code, 0, code.length);
    reassemble(address, code);
  }

  /**
   * Reassemble.
   *
   * @param startAddress start address of code
   * @param code program without start address
   */
  public void reassemble(int startAddress, InputStream code) throws IOException {
    Assert.notNull(code, "Precondition: input != null");

    reassemble(startAddress, FileCopyUtils.copyToByteArray(code));
  }

  /**
   * Reassemble.
   *
   * @param startAddress start address of code
   * @param code program without start address
   */
  public void reassemble(int startAddress, byte[] code) throws IOException {
    Assert.isTrue(startAddress >= 0, "Precondition: startAddress >= 0");
    Assert.notNull(code, "Precondition: code != null");

    if (startAddress == 0x0801) {
      // TODO check for basic header
    }

    commands = new CommandBuffer(code, startAddress);
    reassemble();
  }

  /**
   * Reassemble again.
   */
  public void reassemble() {
    boolean change = true;
    for (int count = 0; change && count < 10; count++) {
      logger.info("Iteration " + count);
      change = detectCodeType();
    }

    commands.tokenize();
  }

  /**
   * Detect type of code.
   *
   * @return whether a code label has been altered
   */
  private boolean detectCodeType() {
    boolean change = false;

    for (IDetector detector : detectors) {
      boolean detectorHit = detector.detect(commands);
      if (detectorHit) {
        logger.info(detector.getClass().getSimpleName() + " changed code types");
      }
      change |= detectorHit;
    }

    // TODO mh: setType() for label locations?
    // TODO mh: setType(OPCODE/CODE) for OpcodeCommands?
    // TODO mh: detect self modifying code
    // TODO mh: detect JSR with parameters afterwards

    return change;
  }

  /**
   * Write commands to output writer.
   *
   * @param output writer to write output to
   */
  public void write(Writer output) throws IOException {
    Assert.notNull(commands, "Precondition: buffer != null");
    Assert.notNull(output, "Precondition: output != null");

    CommandIterator iter = new CommandIterator(commands);

    // start address
    output.append("*=").append(hexWord(commands.getStartAddress())).append("\n");
    output.append("\n");

    // external labels
    Collection<ExternalLabel> externalReferences = new TreeSet<ExternalLabel>(commands.getExternalLabels());
    for (ExternalLabel externalReference : externalReferences) {
      output.append(externalReference.toString()).append(" = ").append(hex(externalReference.getAddress())).append("\n");
    }
    output.append("\n");

    // code
    StringBuilder line = new StringBuilder(80);
    while (iter.hasNextCommand()) {
      ICommand command = iter.nextCommand();
      int pc = command.getAddress();

      line.setLength(0);

      // debug output: prefixes
      if (command == null) {
        line.append("?");
      } else {
        if (!command.isReachable()) {
          line.append("U");
        }
        for (int i = 1; i < command.getSize(); i++) {
          if (commands.hasCodeLabel(pc + i)) {
            line.append("C");
          }
          if (commands.hasDataLabel(pc + i)) {
            line.append("D");
          }
        }
      }
      fillSpaces(line, 5);
      line.append(" | ");

      // debug output: byte representation of command
      line.append(hexWordPlain(pc));
      List<Integer> data = command.toBytes();
      for (int i = 0; i < data.size() && i < 3; i++) {
        line.append(" ");
        line.append(hexBytePlain(data.get(i)));
      }
      fillSpaces(line, 21);
      line.append(data.size() > 3 ? "..." : "   ");
      line.append(" | ");

      // reassembler output
      ILabel label = iter.getLabel();
      if (label != null) {
        // TODO mh: check length of label?
        line.append(label.toString(pc)).append(":");
      }

      fillSpaces(line, 40);
      output.write(line.toString());

      if (command != null) {
        output.append(command.toString(commands));
      } else {
        // TODO mh: log error?
        output.append("???");
      }

      output.append("\n");
    }

    output.flush();
  }

  /**
   * Fill line with spaces up to a limit.
   *
   * @param line line
   * @param num limit
   */
  private void fillSpaces(StringBuilder line, int num) {
    while (line.length() < num) {
      line.append(' ');
    }
  }
}

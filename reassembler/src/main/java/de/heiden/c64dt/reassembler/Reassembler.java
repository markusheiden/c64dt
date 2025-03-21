package de.heiden.c64dt.reassembler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.heiden.c64dt.assembler.CodeBuffer;
import de.heiden.c64dt.reassembler.command.CommandBuffer;
import de.heiden.c64dt.reassembler.command.CommandBufferMapper;
import de.heiden.c64dt.reassembler.command.CommandCreator;
import de.heiden.c64dt.reassembler.detector.BitDetector;
import de.heiden.c64dt.reassembler.detector.BrkDetector;
import de.heiden.c64dt.reassembler.detector.IDetector;
import de.heiden.c64dt.reassembler.detector.JsrDetector;
import de.heiden.c64dt.reassembler.detector.LabelDetector;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.cowwoc.requirements10.java.DefaultJavaValidators.requireThat;

/**
 * Reassembler.
 */
@XmlRootElement(name = "reassembler")
public class Reassembler {
  /**
   * Logger.
   */
  private final Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * Code type detectors to use.
   */
  @XmlElementWrapper(name = "detectors")
  @XmlElements({
    @XmlElement(name = "bit", type = BitDetector.class),
    @XmlElement(name = "brk", type = BrkDetector.class),
    @XmlElement(name = "jsr", type = JsrDetector.class),
    @XmlElement(name = "label", type = LabelDetector.class)
  })
  private final List<IDetector> detectors = new ArrayList<>();

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
    requireThat(detector, "detector").isNotNull();

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
   * Reassemble.
   *
   * @param code Code buffer
   */
  public void reassemble(CodeBuffer code) throws IOException {
    requireThat(code, "code").isNotNull();

    if (code.getCurrentAddress() == 0x0801) {
      // TODO check for basic header
    }

    commands = new CommandBuffer(code.getCode(), code.getCurrentAddress());
    new CommandCreator(commands).createCommands();
    reassemble();
  }

  /**
   * Reassemble again.
   */
  public void reassemble() {
    boolean change = true;
    for (int count = 0; change && count < 10; count++) {
      logger.info("Iteration {}", count);
      change = detectCodeType();
    }

    new CommandCreator(commands).createCommands();
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
        logger.info("{} changed code types", detector.getClass().getSimpleName());
      }
      change |= detectorHit;
    }

    // TODO mh: setType() for label locations?
    // TODO mh: setType(OPCODE/CODE) for OpcodeCommands?
    // TODO mh: detect self modifying code
    // TODO mh: detect JSR with parameters afterwards

    return change;
  }
}

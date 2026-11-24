package de.heiden.c64dt.reassembler.detector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import de.heiden.c64dt.assembler.CodeBuffer;
import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.assembler.Opcode;
import de.heiden.c64dt.reassembler.command.CommandBuffer;
import de.heiden.c64dt.reassembler.command.CommandIterator;
import de.heiden.c64dt.reassembler.command.ICommand;
import de.heiden.c64dt.reassembler.command.OpcodeCommand;
import de.heiden.c64dt.reassembler.command.Subroutine;
import jakarta.xml.bind.annotation.XmlAttribute;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.heiden.c64dt.assembler.OpcodeMode.ABS;
import static de.heiden.c64dt.assembler.OpcodeType.JSR;
import static de.heiden.c64dt.bytes.HexUtil.hexWord;

/**
 * Detects JSR commands to a predefined address, which are followed by fixed length or zero-terminated arguments.
 * Additionally it automatically detects JSR commands which are followed by their zero-terminated arguments.
 */
public class JsrDetector implements IDetector {
  /**
   * Logger.
   */
  private final Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * Minimum number of matches (check for expected argument type) to detect a new subroutine.
   */
  @XmlAttribute(name = "min-matches")
  private int minMatches = 2;

  /**
   * Minimum ratio of unreachable code after JSR to detect a new subroutine.
   */
  @XmlAttribute(name = "unreachable-ratio")
  private double unreachableRatio = 0.2;

  /**
   * Minimum ratio of matches (check for expected argument type) to detect a new subroutine.
   */
  @XmlAttribute(name = "match-ratio")
  private double matchRatio = 0.8;

  /**
   * Maximum length of zero-terminated argument after JSR opcode.
   */
  @XmlAttribute(name = "max-length")
  private int maxLength = 256;

  @Override
  public boolean detect(CommandBuffer commands) {
    var change = false;

    var crossReferences = crossReference(commands);
    for (var crossReference : crossReferences.entrySet()) {
      int address = crossReference.getKey();
      var references = crossReference.getValue();

      var subroutine = commands.getSubroutine(address);
      if (subroutine == null) {
        subroutine = detectZero(commands, address, references);
      }
      if (subroutine == null) {
        subroutine = detectAddress(commands, address, references);
      }
      if (subroutine == null) {
        continue;
      }

      // Mark subroutine calls.
      for (int index : references) {
        var arguments = subroutine.getArguments();
        var type = subroutine.getType();

        if (arguments == 0) {
          logger.debug("Known subroutine with zero terminated argument ({}) at address {}, referenced at index {}", type, hexWord(address), hexWord(index));

          // Search the zero which is terminating the argument.
          var endIndex = search0(commands, index + 3, false);
          if (endIndex < 0) {
            continue;
          }

          change |= markJSR(commands, index, endIndex, type);
        } else if (arguments > 0) {
          logger.debug("Known subroutine with {} byte argument ({}) at address {}, referenced at index {}", arguments, type, hexWord(address), hexWord(index));

          // Fixed length argument.
          change |= markJSR(commands, index, index + 3 + arguments, type);
        }
        // Else: "normal" subroutine.
      }
    }

    return change;
  }

  /**
   * Build JSR cross reference.
   *
   * @param commands Command buffer.
   * @return Absolute address to list of relative addresses of JSR to that absolute address.
   */
  protected Map<Integer, List<Integer>> crossReference(CommandBuffer commands) {
    var result = new HashMap<Integer, List<Integer>>();

    for (var iter = commands.iterator(); iter.hasNext(); ) {
      var command = iter.next();
      if (!(command instanceof OpcodeCommand opcodeCommand)) {
        continue;
      }

      var opcode = opcodeCommand.getOpcode();
      if (command.isReachable() && opcode.getType() == JSR && opcode.getMode() == ABS && iter.hasNext()) {
        var address = opcodeCommand.getArgument();
        result
          .computeIfAbsent(address, k -> new ArrayList<>())
          .add(iter.getIndex());
      }
    }

    return result;
  }

  /**
   * Detect yet unknown subroutines with zero terminated argument.
   *
   * @param commands Command buffer.
   * @param address Address of the subroutine.
   * @param references All references to that subroutine.
   * @return Subroutine or null, if no consistent type of subroutine could be detected.
   */
  protected @Nullable Subroutine detectZero(CommandBuffer commands, int address, List<Integer> references) {
    var matches = 0;
    var unreachable = 0;
    var count = 0;
    for (int index : references) {
      // Try automatic detection of zero-terminated argument.
      var endIndex = search0(commands, index + 3, true);
      if (endIndex >= 0) {
        matches++;
        if (!Objects.requireNonNull(commands.getCommand(index + 3)).isReachable()) {
          unreachable++;
        }
      }
      count++;
    }

    return createSubroutine(commands, "zero terminated argument", address, 0, CodeType.DATA, matches, unreachable, count, true);
  }

  /**
   * Detect yet unknown subroutines with absolute address argument.
   *
   * @param commands Command buffer.
   * @param address Address of the subroutine.
   * @param references All references to that subroutine.
   * @return Subroutine or null, if no consistent type of subroutine could be detected.
   */
  protected @Nullable Subroutine detectAddress(CommandBuffer commands, int address, List<Integer> references) {
    var buffer = new CodeBuffer(commands.getStartAddress(), commands.getCode());

    var matches = 0;
    var unreachable = 0;
    var count = 0;
    for (int index : references) {
      buffer.setCurrentIndex(index);
      if (buffer.has(3 + 2)) {
        buffer.setCurrentIndex(index + 3);
        if (commands.hasAddress(buffer.read(2))) {
          matches++;
          if (!Objects.requireNonNull(commands.getCommand(index + 3)).isReachable()) {
            unreachable++;
          }
        }
      }
      count++;
    }

    // Disabled unreachableRatio for this case.
    return createSubroutine(commands, "absolute address argument", address, 2, CodeType.ADDRESS, matches, unreachable, count, false);
  }

  /**
   * Create new subroutine, if condition matches.
   *
   * @param commands Command buffer.
   * @param kind Textual description of argument type.
   * @param address Absolute address of subroutine.
   * @param arguments Number of argument bytes of subroutine.
   * @param type Code type of argument bytes.
   * @param matches Number of matches.
   * @param unreachable Number of unreachable code after JSR.
   * @param count Total number of JSRs.
   * @param checkUnreachable Should unreachableRatio be checked?
   * @return Subroutine or null, if condition does not match.
   */
  private @Nullable Subroutine createSubroutine(CommandBuffer commands, String kind, int address, int arguments, CodeType type, int matches, int unreachable, int count, boolean checkUnreachable) {
    if (matches == 0) {
      // Nothing detected.
      return null;
    }

    // Check conditions.
    var detectedUnreachableRatio = ((double) unreachable) / ((double) count);
    var detectedMatchRatio = ((double) matches) / ((double) count);
    if (matches < minMatches || checkUnreachable && detectedUnreachableRatio < unreachableRatio || detectedMatchRatio < matchRatio) {
      logger.debug("Potential subroutine with {} at address {}: Probability {} ({}) / {}", kind, hexWord(address), matches, unreachable, count);
      return null;
    }

    // Met all conditions -> create subroutine.
    logger.debug("Detected subroutine with {} at address {}: Probability {} ({}) / {}", kind, hexWord(address), matches, unreachable, count);
    var result = new Subroutine(address, arguments, type);
    commands.addSubroutine(result);

    return result;
  }

  /**
   * Search end of zero-terminated argument.
   * Stops after 256 bytes.
   *
   * @param commands Command buffer.
   * @param startIndex Index to start at.
   * @param check Stop at labels?
   * @return End index or -1, if no arguments have been found.
   */
  private int search0(CommandBuffer commands, int startIndex, boolean check) {
    var code = commands.getCode();
    for (int index = startIndex, count = 0; commands.hasIndex(index) && count < maxLength; index++) {
      // Check for aborting conditions, only if requested.
      if (check) {
        if (commands.hasLabel(commands.addressForIndex(index))) {
          // Stop search at any label.
          return -1;
        }
        var type = commands.getType(index);
        if (!type.isUnknown() && !type.isData()) {
          // Stop if code type has already been determined.
          return -1;
        }
      }

      if (code[index] == 0) {
        // Terminating zero found, return index of following code.
        return index + 1;
      }
    }

    // No valid zero-terminated argument found.
    return -1;
  }

  /**
   * Mark argument bytes of JSR.
   * Adds a code reference from JSR to the opcode after the argument of the JSR for reachability.
   *
   * @param commands Command buffer.
   * @param index Relative address of JSR.
   * @param endIndex End index of data (excl.), next opcode.
   * @param type Code type of argument.
   * @return Whether a change has taken place.
   */
  private boolean markJSR(CommandBuffer commands, int index, int endIndex, CodeType type) {
    if (!commands.hasIndex(endIndex)) {
      return false;
    }

    // Add reference to make code after the argument reachable.
    commands.addCodeReference(index, commands.addressForIndex(endIndex));

    return
      // Mark JSR as opcode.
      commands.setType(index, CodeType.OPCODE) |
        // Mark argument bytes as the given code type.
        commands.setType(index + 3, endIndex, type) |
        // At endIndex the code continues.
        commands.setType(endIndex, CodeType.OPCODE);
  }
}

package de.heiden.c64dt.reassembler.command;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import de.heiden.c64dt.reassembler.label.ExternalLabel;
import de.heiden.c64dt.reassembler.label.ILabel;

import static de.heiden.c64dt.bytes.HexUtil.hex;
import static de.heiden.c64dt.bytes.HexUtil.hexBytePlain;
import static de.heiden.c64dt.bytes.HexUtil.hexWord;
import static de.heiden.c64dt.bytes.HexUtil.hexWordPlain;

/**
 * Write assembler source from {@link CommandBuffer}.
 */
public class CommandWriter {
  /**
   * Writer to write output to.
   */
  private final Writer output;

  /**
   * Constructor.
   *
   * @param output Writer to write output to.
   */
  public CommandWriter(Writer output) {
    this.output = output;
  }

  /**
   * Write commands to output writer.
   *
   * @param commands Command buffer.
   */
  public void write(CommandBuffer commands) throws IOException {
    // Start address.
    output.append("*=").append(hexWord(commands.getStartAddress())).append("\n");
    output.append("\n");

    // External labels.
    var externalReferences = new TreeSet<ExternalLabel>(commands.getExternalLabels());
    for (var externalReference : externalReferences) {
      output.append(externalReference.toString()).append(" = ").append(hex(externalReference.getAddress())).append("\n");
    }
    output.append("\n");

    // Code.
    var line = new StringBuilder(80);
    for (var iter = commands.iterator(); iter.hasNext(); ) {
      var command = iter.next();
      var pc = command.getAddress();

      line.setLength(0);

      // Debug output: prefixes.
      if (command == null) {
        line.append("?");
      } else {
        if (!command.isReachable()) {
          line.append("U");
        }
        for (var i = 1; i < command.getSize(); i++) {
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

      // Debug output: byte representation of command.
      line.append(hexWordPlain(pc));
      var data = command.toBytes();
      for (var i = 0; i < data.size() && i < 3; i++) {
        line.append(" ");
        line.append(hexBytePlain(data.get(i)));
      }
      fillSpaces(line, 21);
      line.append(data.size() > 3 ? "..." : "   ");
      line.append(" | ");

      // Reassembler output.
      var label = iter.getLabel();
      if (label != null) {
        // TODO mh: Check length of label?
        line.append(label.toString(pc)).append(":");
      }

      fillSpaces(line, 40);
      output.write(line.toString());

      if (command != null) {
        output.append(command.toString(commands));
      } else {
        // TODO mh: Log error?
        output.append("???");
      }

      output.append("\n");
    }

    output.flush();
  }

  /**
   * Fill line with spaces up to a limit.
   *
   * @param line Line.
   * @param num Limit.
   */
  private void fillSpaces(StringBuilder line, int num) {
    while (line.length() < num) {
      line.append(' ');
    }
  }
}

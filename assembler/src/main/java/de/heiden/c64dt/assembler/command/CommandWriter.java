package de.heiden.c64dt.assembler.command;

import de.heiden.c64dt.assembler.label.ExternalLabel;
import de.heiden.c64dt.assembler.label.ILabel;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

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
   * @param output writer to write output to
   */
  public CommandWriter(Writer output) {
    Assert.notNull(output, "Precondition: output != null");

    this.output = output;
  }


  /**
   * Write commands to output writer.
   *
   * @param commands Command buffer
   */
  public void write(CommandBuffer commands) throws IOException {
    Assert.notNull(commands, "Precondition: buffer != null");

    // start address
    output.append("*=").append(hexWord(commands.getStartAddress())).append("\n");
    output.append("\n");

    // external labels
    Collection<ExternalLabel> externalReferences = new TreeSet<>(commands.getExternalLabels());
    for (ExternalLabel externalReference : externalReferences) {
      output.append(externalReference.toString()).append(" = ").append(hex(externalReference.getAddress())).append("\n");
    }
    output.append("\n");

    // code
    StringBuilder line = new StringBuilder(80);
    for (CommandIterator iter = commands.iterator(); iter.hasNext(); ) {
      ICommand command = iter.next();
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

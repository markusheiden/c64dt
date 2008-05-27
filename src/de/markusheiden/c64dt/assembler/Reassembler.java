package de.markusheiden.c64dt.assembler;

import org.springframework.util.Assert;

import java.io.Writer;

/**
 * Reassembler.
 */
public class Reassembler {
  public void  reassemble(int startAddress, byte[] input, Writer output) {
    Assert.isTrue(startAddress >= 0, "Precondition: startAddress >= 0");
    Assert.notNull(input, "Precondition: input != null");
    Assert.notNull(output, "Precondition: output != null");

    // TODO check for basic header

    // Convert every byte to its opcode
    Command[] commands = new Command[input.length];
    for (int i = 0; i < input.length; i++) {
      byte b = input[i];
      commands[i] = null;
    }
  }
}

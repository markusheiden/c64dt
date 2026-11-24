package de.heiden.c64dt.reassembler.detector;

import de.heiden.c64dt.assembler.Opcode;
import de.heiden.c64dt.reassembler.command.CommandBuffer;
import de.heiden.c64dt.reassembler.command.OpcodeCommand;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for {@link JsrDetector}.
 */
public class JsrDetectorTest {
  @Test
  public void testCrossReference() {
    var commands = new CommandBuffer(new byte[10], 0x1000);
    var jsr0 = new OpcodeCommand(Opcode.OPCODE_20, 0x1000);
    jsr0.setReachable(true);
    commands.setCommand(0, jsr0);
    var jsr3 = new OpcodeCommand(Opcode.OPCODE_20, 0x1003);
    jsr3.setReachable(true);
    commands.setCommand(3, jsr3);
    var jsr6 = new OpcodeCommand(Opcode.OPCODE_20, 0x1000);
    jsr6.setReachable(true);
    commands.setCommand(6, jsr6);
    commands.setCommand(9, new OpcodeCommand(Opcode.OPCODE_EA));

    var detector = new JsrDetector();
    var crossReference = detector.crossReference(commands);

    var references1000 = crossReference.get(0x1000);
    // JSR at relative address 0 references $1000.
    assertThat(references1000).contains(0);
    // JSR at relative address 6 references $1000.
    assertThat(references1000).contains(6);

    assertThat(references1000).hasSize(2);

    var references1003 = crossReference.get(0x1003);
    // JSR at relative address 3 references $1003.
    assertThat(references1003).contains(3);

    assertThat(references1003).hasSize(1);
    assertThat(crossReference).hasSize(2);
  }
}

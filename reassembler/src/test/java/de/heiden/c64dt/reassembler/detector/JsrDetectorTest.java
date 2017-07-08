package de.heiden.c64dt.reassembler.detector;

import de.heiden.c64dt.assembler.Opcode;
import de.heiden.c64dt.reassembler.command.CommandBuffer;
import de.heiden.c64dt.reassembler.command.OpcodeCommand;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link JsrDetector}
 */
public class JsrDetectorTest {
  @Test
  public void testCrossReference() {
    CommandBuffer commands = new CommandBuffer(new byte[10], 0x1000);
    OpcodeCommand jsr0 = new OpcodeCommand(Opcode.OPCODE_20, 0x1000);
    jsr0.setReachable(true);
    commands.setCommand(0, jsr0);
    OpcodeCommand jsr3 = new OpcodeCommand(Opcode.OPCODE_20, 0x1003);
    jsr3.setReachable(true);
    commands.setCommand(3, jsr3);
    OpcodeCommand jsr6 = new OpcodeCommand(Opcode.OPCODE_20, 0x1000);
    jsr6.setReachable(true);
    commands.setCommand(6, jsr6);
    commands.setCommand(9, new OpcodeCommand(Opcode.OPCODE_EA));

    JsrDetector detector = new JsrDetector();
    Map<Integer, List<Integer>> crossReference = detector.crossReference(commands);

    List<Integer> references1000 = crossReference.get(0x1000);
    // JSR at relative address 0 references $1000
    assertTrue(references1000.contains(0));
    // JSR at relative address 6 references $1000
    assertTrue(references1000.contains(6));

    assertEquals(2, references1000.size());

    List<Integer> references1003 = crossReference.get(0x1003);
    // JSR at relative address 3 references $1003
    assertTrue(references1003.contains(3));

    assertEquals(1, references1003.size());
    assertEquals(2, crossReference.size());
  }
}

package de.heiden.c64dt.assembler.detector;

import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.assembler.Opcode;
import de.heiden.c64dt.assembler.OpcodeType;
import de.heiden.c64dt.assembler.command.BitCommand;
import de.heiden.c64dt.assembler.command.CommandBuffer;
import de.heiden.c64dt.assembler.command.ICommand;
import de.heiden.c64dt.assembler.command.OpcodeCommand;

import java.util.List;

/**
 * Detects bit commands which are intended to just skip the next opcode.
 */
public class BitDetector implements IDetector
{
  @Override
  public boolean detect(CommandBuffer commands)
  {
    boolean change = false;

    commands.restart();
    while (commands.hasNextCommand())
    {
      ICommand command = commands.nextCommand();
      if (command instanceof OpcodeCommand)
      {
        OpcodeCommand opcodeCommand = (OpcodeCommand) command;
        Opcode opcode = opcodeCommand.getOpcode();
        int size = opcodeCommand.getSize();

        if (opcode.getType().equals(OpcodeType.BIT) && size > 1 && commands.hasCodeLabel(opcodeCommand.getAddress() + 1))
        {
          List<Integer> bytes = opcodeCommand.toBytes();
          Opcode skippedOpcode = Opcode.opcode(bytes.get(1));

          if (skippedOpcode.isLegal() && skippedOpcode.getSize() == size - 1)
          {
            int index = commands.getCurrentIndex();
            change |= commands.setType(index++, CodeType.BIT);
            change |= commands.setType(index++, CodeType.OPCODE);
            if (bytes.size() == 3) {
              change |= commands.setType(index++, CodeType.CODE);
            }
          }
        }
      }
    }

    return change;
  }
}

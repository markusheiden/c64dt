package de.heiden.c64dt.assembler.detector;

import de.heiden.c64dt.assembler.CodeBuffer;
import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.assembler.Opcode;
import de.heiden.c64dt.assembler.OpcodeMode;
import de.heiden.c64dt.assembler.command.AddressCommand;
import de.heiden.c64dt.assembler.command.BitCommand;
import de.heiden.c64dt.assembler.command.CommandBuffer;
import de.heiden.c64dt.assembler.command.DataCommand;
import de.heiden.c64dt.assembler.command.OpcodeCommand;

/**
 * Tokenizes the code.
 * Should be the first detector.
 */
public class Tokenizer implements IDetector
{
  @Override
  public boolean detect(CommandBuffer commands)
  {
    CodeBuffer code = new CodeBuffer(commands.getCode());

    commands.clear();
    while (code.has(1))
    {
      int index = commands.getIndex();
      int pc = commands.addressForIndex(index);
      CodeType type = commands.getType(index);

      if (type == CodeType.BIT)
      {
        Opcode opcode = code.readOpcode();
        // TODO mh: read ahead argument
        commands.addCommand(new BitCommand(opcode, 0));
      }
      else if (type == CodeType.ABSOLUTE_ADDRESS)
      {
        // absolute address reference as data
        int address = code.read(2);
        commands.addCommand(new AddressCommand(address));
        commands.addCodeReference(index, address);

      }
      else if (type == CodeType.DATA)
      {
        // plain data
        commands.addCommand(new DataCommand(code.readByte()));
      }
      else
      {
        // unknown or code -> try to disassemble an opcode
        Opcode opcode = code.readOpcode();
        OpcodeMode mode = opcode.getMode();
        int size = mode.getSize();

        if (code.has(1 + size))
        {
          if (opcode.isLegal() || type == CodeType.OPCODE)
          {
            // TODO log error if illegal opcode and type is OPCODE?
            if (size == 0)
            {
              // opcode without argument
              commands.addCommand(new OpcodeCommand(opcode));
            }
            else
            {
              // opcode with an argument
              int argument = code.read(mode.getSize());
              commands.addCommand(new OpcodeCommand(opcode, argument));
              if (mode.isAddress())
              {
                int address = mode.getAddress(pc, argument);
                // track references of opcodes
                commands.addReference(opcode.getType().isJump(), index, address);
              }
            }
          }
          else
          {
            // no valid opcode -> assume data
            commands.addCommand(new DataCommand(opcode.getOpcode()));
          }
        }
        else
        {
          // not enough argument bytes for opcode -> assume data
          // TODO log error, when type != UNKNOWN?
          commands.addCommand(new DataCommand(opcode.getOpcode()));
        }
      }
    }

    // initial creation of commands does not change the code types
    return false;
  }
}

package de.heiden.c64dt.assembler.detector;

import de.heiden.c64dt.assembler.CodeBuffer;
import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.assembler.Opcode;
import de.heiden.c64dt.assembler.OpcodeMode;
import de.heiden.c64dt.assembler.OpcodeType;
import de.heiden.c64dt.assembler.command.AddressCommand;
import de.heiden.c64dt.assembler.command.BitCommand;
import de.heiden.c64dt.assembler.command.CommandBuffer;
import de.heiden.c64dt.assembler.command.DataCommand;
import de.heiden.c64dt.assembler.command.OpcodeCommand;
import org.springframework.util.Assert;

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
      int codeIndex = code.getCurrentIndex();

      int index = commands.getIndex();
      Assert.isTrue(codeIndex == index, "Check: codeIndex == index");
      int pc = commands.addressForIndex(index);
      CodeType type = commands.getType(index);

      if (type == CodeType.BIT)
      {
        // BIT opcode used just to skip the next opcode
        Opcode opcode = code.readOpcode();
        int modeSize = opcode.getMode().getSize();

        if (opcode.getType().equals(OpcodeType.BIT) && modeSize > 0 && code.has(modeSize))
        {
          int argumentIndex = code.getCurrentIndex();
          commands.addCommand(new BitCommand(opcode, code.read(modeSize)));
          // Reset code buffer to the argument, because this should be the skipped opcode
          code.setCurrentIndex(argumentIndex);
        }
        else
        {
          // no BIT opcode -> assume data
          code.setCurrentIndex(codeIndex);
          commands.addCommand(new DataCommand(code.readByte()));
        }
      }
      else if (type == CodeType.ABSOLUTE_ADDRESS)
      {
        // absolute address as data
        int address;
        if (code.has(2) && commands.hasAddress(address = code.read(2)))
        {
          commands.addCommand(new AddressCommand(address));
          commands.addCodeReference(index, address);
        }
        else
        {
          code.setCurrentIndex(codeIndex);
          commands.addCommand(new DataCommand(code.readByte(), code.readByte()));
        }
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
        int modeSize = mode.getSize();

        if (code.has(modeSize) && (opcode.isLegal() || type == CodeType.OPCODE))
        {
          // TODO mh: log error if illegal opcode and type is OPCODE?
          int argument = code.read(modeSize);
          commands.addCommand(new OpcodeCommand(opcode, argument));
          if (mode.isAddress())
          {
            int address = mode.getAddress(pc, argument);
            // track references of opcodes
            commands.addReference(opcode.getType().isJump(), index, address);
          }
        }
        else
        {
          // not enough argument bytes for opcode or illegal opcode -> assume data
          code.setCurrentIndex(codeIndex);
          commands.addCommand(new DataCommand(code.readByte()));
        }
      }
    }

    // initial creation of commands does not change the code types
    return false;
  }
}

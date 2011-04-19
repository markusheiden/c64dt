package de.heiden.c64dt.assembler.detector;

import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.assembler.OpcodeType;
import de.heiden.c64dt.assembler.command.CommandBuffer;
import de.heiden.c64dt.assembler.command.ICommand;
import de.heiden.c64dt.assembler.command.OpcodeCommand;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * Detects jsr commands to predefined address which are followed by fixed length arguments.
 */
public class JsrDetector implements IDetector
{
  @Override
  public boolean detect(CommandBuffer commands)
  {
    boolean change = false;

    commands.restart();
    while (commands.hasNextCommand())
    {
      int index = commands.getCurrentIndex();
      ICommand command = commands.nextCommand();
      if (command instanceof OpcodeCommand)
      {
        OpcodeCommand opcodeCommand = (OpcodeCommand) command;

        if (opcodeCommand.getOpcode().getType().equals(OpcodeType.JSR) && commands.hasSubroutine(opcodeCommand.getArgument()))
        {
          int startIndex = commands.getCurrentIndex();
          int endIndex = startIndex + commands.getSubroutineArguments(opcodeCommand.getArgument());
            // Mark argument bytes as data
            commands.setType(startIndex, endIndex, CodeType.DATA);
            // At endIndex the code continues
            commands.setType(endIndex, CodeType.OPCODE);
            // Add reference to make code reachable
            commands.addCodeReference(index, commands.addressForIndex(endIndex));
            change = true;
        }
      }
    }

    return change;
  }
}

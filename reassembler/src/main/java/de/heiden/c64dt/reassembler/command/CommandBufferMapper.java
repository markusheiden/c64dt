package de.heiden.c64dt.reassembler.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;

import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.reassembler.xml.HexByteAdapter;
import de.heiden.c64dt.reassembler.xml.HexWordAdapter;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.jspecify.annotations.Nullable;

import static com.github.cowwoc.requirements10.java.DefaultJavaValidators.requireThat;

/**
 * XML-Mapper for {@link CommandBuffer}.
 */
@XmlType
public class CommandBufferMapper extends XmlAdapter<CommandBufferMapper, CommandBuffer> {
  @XmlElement(name = "code")
  @SuppressWarnings("NullAway.Init")
  private byte[] code;

  @XmlElement(name = "address")
  @XmlElementWrapper(name = "addresses")
  private final List<AddressMapper> addresses = new ArrayList<>();

  @XmlType
  public static class AddressMapper {
    @XmlAttribute
    @XmlJavaTypeAdapter(type = int.class, value = HexWordAdapter.class)
    private int index;
    @XmlAttribute
    @XmlJavaTypeAdapter(type = int.class, value = HexWordAdapter.class)
    private int base;
  }

  @XmlElement(name = "subroutine")
  @XmlElementWrapper(name = "subroutines")
  private final List<SubroutineMapper> subroutines = new ArrayList<>();

  @XmlType
  public static class SubroutineMapper {
    @XmlAttribute
    @XmlJavaTypeAdapter(type = int.class, value = HexWordAdapter.class)
    private int address;
    @XmlAttribute
    @XmlJavaTypeAdapter(type = int.class, value = HexByteAdapter.class)
    private int arguments;
    @XmlValue
    @SuppressWarnings("NullAway.Init")
    private CodeType type;
  }

  @XmlElement(name = "type")
  @XmlElementWrapper(name = "types")
  private final List<TypeMapper> types = new ArrayList<>();

  @XmlType
  public static class TypeMapper {
    @XmlAttribute
    @XmlJavaTypeAdapter(type = int.class, value = HexWordAdapter.class)
    private int index;
    @XmlAttribute
    @XmlJavaTypeAdapter(type = Integer.class, value = HexWordAdapter.class)
    private @Nullable Integer end;
    @XmlValue
    @SuppressWarnings("NullAway.Init")
    private CodeType type;
  }

  @Override
  public CommandBufferMapper marshal(CommandBuffer commands) throws Exception {
    var result = new CommandBufferMapper();

    result.code = commands.getCode();

    // Start addresses.
    var startAddresses = commands.getStartAddresses();
    for (var entry : startAddresses.entrySet()) {
      var address = new AddressMapper();
      address.index = entry.getKey();
      address.base = entry.getValue();
      result.addresses.add(address);
    }

    // Subroutines.
    for (var entry : commands.getSubroutines().entrySet()) {
      var subroutine = new SubroutineMapper();
      subroutine.address = entry.getKey();
      subroutine.arguments = entry.getValue().getArguments();
      subroutine.type = entry.getValue().getType();
      result.subroutines.add(subroutine);
    }

    // Detected code types.
    for (var index = 0; index < commands.getLength(); ) {
      var startIndex = index;
      var type = commands.getType(index++);
      if (type.isUnknown()) {
        continue;
      }

      // Count indexes with the same type.
      var count = 1;
      for (; index < commands.getLength(); index++, count++) {
        if (!commands.getType(index).equals(type)) {
          break;
        }
      }

      var codeType = new TypeMapper();
      codeType.index = startIndex;
      if (count > 1) {
        codeType.end = index;
      }
      codeType.type = type;

      result.types.add(codeType);
    }

    return result;
  }

  @Override
  public CommandBuffer unmarshal(CommandBufferMapper xmlCommands) throws Exception {
    var addresses = xmlCommands.addresses;

    // Start address / first base address.
    var firstAddress = addresses.get(0);
    var startIndex = firstAddress.index;
    requireThat(startIndex, "startIndex").isEqualTo(0);

    // The start address is the first base address and automatically sets the end base address.
    var commands = new CommandBuffer(xmlCommands.code, firstAddress.base);

    // Remaining base addresses.
    for (var i = 1; i < addresses.size() - 1; i++) {
      var address = addresses.get(i);
      commands.rebase(address.index, address.base);
    }

    // End base address.
    var lastAddress = addresses.get(addresses.size() - 1);
    requireThat(lastAddress.index, "lastAddress.index").isEqualTo(xmlCommands.code.length, "xmlCommands.code.length");
    requireThat(lastAddress.base, "lastAddress.base").isEqualTo(firstAddress.base, "firstAddress.base");

    // Subroutines.
    for (var subroutineMapper : xmlCommands.subroutines) {
      commands.addSubroutine(new Subroutine(subroutineMapper.address, subroutineMapper.arguments, subroutineMapper.type));
    }

    // Detected code types.
    for (var typeMapper : xmlCommands.types) {
      if (typeMapper.end != null) {
        commands.setType(typeMapper.index, typeMapper.end, typeMapper.type);
      } else {
        commands.setType(typeMapper.index, typeMapper.type);
      }
    }

    new CommandCreator(commands).createCommands();

    return commands;
  }
}

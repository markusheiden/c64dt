package de.heiden.c64dt.reassembler.command;

import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.reassembler.xml.HexByteAdapter;
import de.heiden.c64dt.reassembler.xml.HexWordAdapter;
import org.springframework.util.Assert;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;

/**
 * XML-Mapper for {@link CommandBuffer}.
 */
@XmlType
public class CommandBufferMapper extends XmlAdapter<CommandBufferMapper, CommandBuffer> {
  @XmlElement(name = "code")
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
    private Integer end;
    @XmlValue
    private CodeType type;
  }

  @Override
  public CommandBufferMapper marshal(CommandBuffer commands) throws Exception {
    CommandBufferMapper result = new CommandBufferMapper();

    result.code = commands.getCode();

    // start addresses
    SortedMap<Integer, Integer> startAddresses = commands.getStartAddresses();
    for (Entry<Integer, Integer> entry : startAddresses.entrySet()) {
      AddressMapper address = new AddressMapper();
      address.index = entry.getKey();
      address.base = entry.getValue();
      result.addresses.add(address);
    }

    // subroutines
    for (Entry<Integer, Subroutine> entry : commands.getSubroutines().entrySet()) {
      SubroutineMapper subroutine = new SubroutineMapper();
      subroutine.address = entry.getKey();
      subroutine.arguments = entry.getValue().getArguments();
      subroutine.type = entry.getValue().getType();
      result.subroutines.add(subroutine);
    }

    // detected code types
    for (int index = 0; index < commands.getLength(); ) {
      int startIndex = index;
      CodeType type = commands.getType(index++);
      if (type.isUnknown()) {
        continue;
      }

      // count indexes with the same type
      int count = 1;
      for (; index < commands.getLength(); index++, count++) {
        if (!commands.getType(index).equals(type)) {
          break;
        }
      }

      TypeMapper codeType = new TypeMapper();
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
    List<AddressMapper> addresses = xmlCommands.addresses;

    // start address / first base address
    AddressMapper firstAddress = addresses.get(0);
    int startIndex = firstAddress.index;
    Assert.isTrue(startIndex == 0, "Check: startIndex == 0");

    // the start address is the first base address and automatically sets the end base address
    CommandBuffer commands = new CommandBuffer(xmlCommands.code, firstAddress.base);

    // remaining base addresses
    for (int i = 1; i < addresses.size() - 1; i++) {
      AddressMapper address = addresses.get(i);
      commands.rebase(address.index, address.base);
    }

    // end base address
    AddressMapper lastAddress = addresses.get(addresses.size() - 1);
    Assert.isTrue(lastAddress.index == xmlCommands.code.length, "Check: end index == code.length");
    Assert.isTrue(lastAddress.base == firstAddress.base, "Check: end address == start address");

    // subroutines
    for (SubroutineMapper subroutineMapper : xmlCommands.subroutines) {
      commands.addSubroutine(new Subroutine(subroutineMapper.address, subroutineMapper.arguments, subroutineMapper.type));
    }

    // detected code types
    for (TypeMapper typeMapper : xmlCommands.types) {
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

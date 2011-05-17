package de.heiden.c64dt.assembler.gui;

import de.heiden.c64dt.assembler.Reassembler;
import de.heiden.c64dt.assembler.command.CommandBuffer;
import de.heiden.c64dt.assembler.command.CommandIterator;
import de.heiden.c64dt.assembler.command.ICommand;
import de.heiden.c64dt.assembler.label.ILabel;

import javax.swing.table.DefaultTableModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.heiden.c64dt.util.HexUtil.hexBytePlain;
import static de.heiden.c64dt.util.HexUtil.hexWordPlain;

/**
 * Table model for code view.
 */
public class CodeTableModel extends DefaultTableModel
{
  /**
   * Underlying representation: the reassembler.
   */
  private Reassembler reassembler;

  /**
   * Mapping from row to relative address of the code shown in that row.
   */
  private final Map<Integer, Integer> rowToIndex = new HashMap<Integer, Integer>();

  /**
   * Constructor.
   */
  public CodeTableModel()
  {
    super(new String[]{"Flags", "Addr", "Bytes", "Label", "Code"}, 0);
  }

  /**
   * Get underlying reassembler.
   */
  public Reassembler getReassembler()
  {
    return reassembler;
  }

  /**
   * Use another reassembler.
   *
   * @param reassembler Underlying representation
   */
  public void use(Reassembler reassembler)
  {
    this.reassembler = reassembler;
    update();
  }

  /**
   * Update table model from commands.
   */
  public void update()
  {
    // Clear old model
    setRowCount(0);
    rowToIndex.clear();

    // no model -> no representation
    if (reassembler == null) {
      return;
    }

    reassembler.reassemble();
    CommandBuffer commands = reassembler.getCommands();

    StringBuilder builder = new StringBuilder();

    CommandIterator iter = new CommandIterator(commands);
    while (iter.hasNextCommand())
    {
      ICommand command = iter.nextCommand();
      int index = iter.getIndex();
      int addr = commands.addressForIndex(index);

      builder.setLength(0);
      if (!command.isReachable())
      {
        builder.append("U");
      }
      for (int i = 1; i < command.getSize(); i++)
      {
        if (commands.hasCodeLabel(addr + i))
        {
          builder.append("C");
        }
        if (commands.hasDataLabel(addr + i))
        {
          builder.append("D");
        }
      }
      String flags = builder.toString();

      builder.setLength(0);
      List<Integer> data = command.toBytes();
      for (int dataByte : data)
      {
        builder.append(" ");
        builder.append(hexBytePlain(dataByte));
      }
      String bytes = builder.toString().trim();

      builder.setLength(0);
      ILabel label = iter.getLabel();
      if (label != null)
      {
        // TODO mh: check length of label?
        builder.append(label.toString(addr)).append(":");
      }
      String labelString = builder.toString();

      builder.setLength(0);
      if (command != null)
      {
        builder.append(command.toString(commands));
      }
      else
      {
        // TODO mh: log error?
        builder.append("???");
      }
      String code = builder.toString();

      addRow(index, flags, addr, bytes, labelString, code);
    }
  }

  /**
   * Add a row to the model.
   *
   * @param index Relative address
   * @param flags Flags
   * @param address Absolute address
   * @param bytes Bytes
   * @param label Label
   * @param code Code
   */
  private void addRow(int index, String flags, int address, String bytes, String label, String code)
  {
    rowToIndex.put(getRowCount(), index);
    addRow(new Object[]{flags, hexWordPlain(address), bytes, label, code});
  }

  /**
   * The relative address of the code shown in a given row.
   *
   * @param row Row
   */
  public Integer getIndex(int row)
  {
    return rowToIndex.get(row);
  }
}

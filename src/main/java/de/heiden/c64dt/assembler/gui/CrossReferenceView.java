package de.heiden.c64dt.assembler.gui;

import de.heiden.c64dt.assembler.Reassembler;
import de.heiden.c64dt.assembler.command.CommandBuffer;
import org.springframework.util.Assert;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.SortedSet;

import static de.heiden.c64dt.util.HexUtil.hexWordPlain;

/**
 * View for cross reference of current opcode.
 */
public class CrossReferenceView
{
  /**
   * Model.
   */
  private DefaultTableModel model;

  /**
   * Underlying representation: the reassembler.
   */
  private Reassembler reassembler;

  /**
   * Constructor.
   */
  public CrossReferenceView()
  {
    this.model = new DefaultTableModel();
    model.addColumn("Index");
    model.addColumn("Addr");
  }

  /**
   * Use another reassembler.
   *
   * @param reassembler
   */
  public void use(Reassembler reassembler)
  {
    Assert.notNull(reassembler, "Precondition: reassembler != null");

    this.reassembler = reassembler;
  }

  /**
   * Select a relative address to show the cross reference for.
   *
   * @param index Relative address
   */
  public void select(int index)
  {
    model.setRowCount(0);
    if (index < 0)
    {
      // no row selected -> display nothing
      return;
    }

    CommandBuffer commands = reassembler.getCommands();
    SortedSet<Integer> references = commands.getReferences(commands.addressForIndex(index));
    for (Integer reference : references)
    {
      model.addRow(new Object[]{hexWordPlain(reference), hexWordPlain(commands.addressForIndex(reference))});
    }
  }

  /**
   * Create GUI representation.
   */
  public JComponent createComponent()
  {
    final JTable table = new JTable(model);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    TableColumnModel columnModel = table.getColumnModel();
    columnModel.getColumn(0).setMaxWidth(40);
    columnModel.getColumn(1).setMaxWidth(40);
    table.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    JScrollPane scroll = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scroll.setBorder(BorderFactory.createTitledBorder("References"));

    return scroll;
  }
}

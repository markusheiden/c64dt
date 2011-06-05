package de.heiden.c64dt.assembler.gui;

import de.heiden.c64dt.assembler.Reassembler;
import org.springframework.util.Assert;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.awt.*;

/**
 * View for cross reference of current opcode.
 */
public class CrossReferenceView
{
  /**
   * Model.
   */
  private CrossReferenceTableModel model;

  /**
   * Constructor.
   */
  public CrossReferenceView()
  {
    this.model = new CrossReferenceTableModel();
  }

  /**
   * Use another reassembler.
   *
   * @param reassembler
   */
  public void use(Reassembler reassembler)
  {
    Assert.notNull(reassembler, "Precondition: reassembler != null");

    model.use(reassembler);
  }

  /**
   * Select a relative address to show the cross reference for.
   *
   * @param index Relative address
   */
  public void select(int index)
  {
    model.select(index);
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

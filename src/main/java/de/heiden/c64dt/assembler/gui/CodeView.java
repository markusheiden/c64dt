package de.heiden.c64dt.assembler.gui;

import de.heiden.c64dt.assembler.Reassembler;
import de.heiden.c64dt.assembler.gui.action.CodeTypeActions;
import de.heiden.c64dt.assembler.gui.action.GotoActions;
import org.springframework.util.Assert;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * View for reassembled code.
 */
public class CodeView
{
  /**
   * Model.
   */
  private CodeTableModel model;

  /**
   * Context menu.
   */
  private JPopupMenu contextMenu;

  /**
   * Constructor.
   */
  public CodeView()
  {
    model = new CodeTableModel();
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
   * Create GUI representation.
   */
  public JInternalFrame createComponent()
  {
    JInternalFrame frame = new JInternalFrame("Code");
    frame.setVisible(true);

    final JTable table = new JTable(model);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
    TableColumnModel columnModel = table.getColumnModel();
    columnModel.getColumn(0).setMaxWidth(40);
    columnModel.getColumn(1).setMaxWidth(40);
    columnModel.getColumn(2).setPreferredWidth(100);
    columnModel.getColumn(3).setMaxWidth(100);
    columnModel.getColumn(4).setPreferredWidth(200);
    table.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
    table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

    contextMenu = new JPopupMenu();
    new GotoActions(table).addToMenu(contextMenu);
    contextMenu.add(new JSeparator());
    new CodeTypeActions(table).addToMenu(contextMenu);

    table.addMouseListener(new MouseAdapter()
    {
      @Override
      public void mouseReleased(MouseEvent e)
      {
        if ((e.getButton() & MouseEvent.BUTTON2) == 0)
        {
          // not right mouse button -> ignore
          return;
        }

        // select row on click of the right mouse button too
        int row = table.rowAtPoint(e.getPoint());
        if (row >= 0 && !table.getSelectionModel().isSelectedIndex(row))
        {
          table.getSelectionModel().setSelectionInterval(row, row);
        }

        // show (dynamic) context menu
        contextMenu.show(table, e.getX(), e.getY());
      }
    });

    frame.add(new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));

    return frame;
  }
}

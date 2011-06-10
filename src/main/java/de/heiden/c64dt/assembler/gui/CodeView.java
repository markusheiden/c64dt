package de.heiden.c64dt.assembler.gui;

import de.heiden.c64dt.assembler.Reassembler;
import de.heiden.c64dt.assembler.gui.action.CodeTypeActions;
import de.heiden.c64dt.assembler.gui.action.GotoActions;
import de.heiden.c64dt.assembler.gui.event.AddressChangedEvent;
import de.heiden.c64dt.assembler.gui.event.GotoAddressEvent;
import de.heiden.c64dt.assembler.gui.event.ReassemblerEvent;
import de.heiden.c64dt.assembler.gui.util.TableUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * View for reassembled code.
 */
@Component
public class CodeView implements ApplicationListener<ReassemblerEvent>
{
  /**
   * The table.
   */
  private JTable table;

  /**
   * Model.
   */
  private CodeTableModel model;

  /**
   * Context menu.
   */
  private JPopupMenu contextMenu;

  /**
   * Spring event publisher.
   */
  @Autowired
  private ApplicationEventPublisher publisher;

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
  public JComponent createComponent()
  {
    table = new JTable(model);
    TableColumnModel columnModel = table.getColumnModel();
    columnModel.getColumn(0).setMaxWidth(40);
    columnModel.getColumn(1).setMaxWidth(40);
    columnModel.getColumn(2).setMaxWidth(40);
    columnModel.getColumn(3).setPreferredWidth(280);
    columnModel.getColumn(3).setMaxWidth(280);
    columnModel.getColumn(4).setMaxWidth(100);
    columnModel.getColumn(5).setPreferredWidth(200);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
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

    table.getSelectionModel().addListSelectionListener(new ListSelectionListener()
    {
      @Override
      public void valueChanged(ListSelectionEvent listSelectionEvent)
      {
        publisher.publishEvent(new AddressChangedEvent(this,
          table.getSelectedRow() >= 0? model.getIndex(table.getSelectedRow()) : -1));
      }
    });

    JScrollPane scroll = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scroll.setBorder(BorderFactory.createTitledBorder("Code"));

    return scroll;
  }

  @Override
  public void onApplicationEvent(ReassemblerEvent event)
  {
    if (event instanceof GotoAddressEvent)
    {
      gotoIndex(((GotoAddressEvent) event).getIndex());
    }
  }

  /**
   * Jump to the given relative address.
   *
   * @param index Relative address
   */
  public void gotoIndex(int index)
  {
    if (!model.getReassembler().getCommands().hasIndex(index))
    {
      return;
    }

    int row = model.getRow(index);
    table.scrollRectToVisible(TableUtil.getRowBounds(table, row, 3));
    table.getSelectionModel().setSelectionInterval(row, row);
  }
}

package de.heiden.c64dt.assembler.gui.action;

import de.heiden.c64dt.assembler.command.CommandBuffer;
import de.heiden.c64dt.assembler.command.ICommand;
import de.heiden.c64dt.assembler.command.OpcodeCommand;
import de.heiden.c64dt.assembler.gui.CodeTableModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * All types of "go to" actions
 */
public class GotoActions
{
  /**
   * The table.
   */
  private final JTable table;

  /**
   * Action "Go to".
   */
  private final Action gotoAction;

  /**
   * Constructor.
   *
   * @param table The table this action works on
   */
  public GotoActions(final JTable table)
  {
    this.table = table;

    gotoAction = createGotoAction();

    table.getSelectionModel().addListSelectionListener(new ListSelectionListener()
    {
      @Override
      public void valueChanged(ListSelectionEvent listSelectionEvent)
      {
        gotoAction.setEnabled(table.getSelectedRowCount() == 1);
      }
    });

    table.addMouseListener(new MouseAdapter()
    {
      @Override
      public void mouseClicked(MouseEvent e)
      {
        if (e.getClickCount() == 2)
        {
          gotoDestination(table.rowAtPoint(e.getPoint()));
        }
      }
    });
  }

  /**
   * Add the actions to a menu.
   *
   * @param menu Menu
   */
  public void addToMenu(JPopupMenu menu)
  {
    menu.add(gotoAction);
  }

  /**
   * Create action "Go to".
   */
  private Action createGotoAction()
  {
    return new AbstractAction("Go to")
    {
      @Override
      public void actionPerformed(ActionEvent actionEvent)
      {
        gotoDestination(table.getSelectedRow());
      }
    };
  }

  /**
   * Goto destination (address) of an opcode.
   *
   * @param row Row in which the event has been triggered or -1
   */
  private void gotoDestination(int row)
  {
    if (row < 0)
    {
      // now row -> no jump
      return;
    }

    CodeTableModel model = (CodeTableModel) table.getModel();
    CommandBuffer commands = model.getReassembler().getCommands();

    int index = model.getIndex(row);

    // just works for opcodes
    ICommand command = commands.getCommand(index);
    if (!(command instanceof OpcodeCommand))
    {
      return;
    }

    // just works for opcodes with an address
    OpcodeCommand opcodeCommand = (OpcodeCommand) command;
    if (!opcodeCommand.getOpcode().getMode().isAddress())
    {
      return;
    }

    // check if address is known
    int address = opcodeCommand.getOpcode().getMode().getAddress(opcodeCommand.getAddress(), opcodeCommand.getArgument());
    if (!commands.hasAddress(address))
    {
      return;
    }

    // do the jump
    int jumpIndex = commands.indexForAddress(address);
    int jumpRow = model.getRow(jumpIndex);
    table.scrollRectToVisible(getRowBounds(table, jumpRow));
    table.getSelectionModel().setSelectionInterval(jumpRow, jumpRow);
  }

  /**
   * Get bounds for row +/- 3 rows.
   *
   * @param table Table
   * @param row row
   */
  private Rectangle getRowBounds(JTable table, int row)
  {
    int first = Math.max(0, row - 3);
    int last = Math.min(table.getRowCount() - 1, row + 3);

    Rectangle result = table.getCellRect(first, -1, true);
    result = result.union(table.getCellRect(last, -1, true));
    Insets i = table.getInsets();

    result.x = i.left;
    result.width = table.getWidth() - i.left - i.right;

    return result;
  }
}

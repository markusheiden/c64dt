package de.heiden.c64dt.assembler.gui.action;

import de.heiden.c64dt.assembler.command.CommandBuffer;
import de.heiden.c64dt.assembler.command.ICommand;
import de.heiden.c64dt.assembler.command.OpcodeCommand;
import de.heiden.c64dt.assembler.gui.CodeTableModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
   * Constructor.
   *
   * @param table The table this action works on
   */
  public GotoActions(JTable table)
  {
    this.table = table;
  }

  /**
   * Add the actions to a menu.
   *
   * @param menu Menu
   */
  public void addToMenu(JPopupMenu menu)
  {
    final Action gotoAction = gotoAction();

    menu.add(gotoAction);

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
   * Create action "Go to".
   */
  public Action gotoAction()
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
   * @param row Row in which the event has been triggered
   */
  private void gotoDestination(int row)
  {
    if (row < 0)
    {
      return;
    }

    CodeTableModel model = (CodeTableModel) table.getModel();
    CommandBuffer commands = model.getReassembler().getCommands();

    int index = model.getIndex(row);
    ICommand command = commands.getCommand(index);
    if (command instanceof OpcodeCommand)
    {
      OpcodeCommand opcodeCommand = (OpcodeCommand) command;
      if (opcodeCommand.getOpcode().getMode().isAddress())
      {
        int address = opcodeCommand.getArgument();
        if (commands.hasAddress(address))
        {
          int jumpIndex = commands.indexForAddress(address);
          int jumpRow = model.getRow(jumpIndex);
          table.scrollRectToVisible(table.getCellRect(jumpRow, 1, true));
        }
      }
    }
  }

}

package de.heiden.c64dt.assembler.gui.action;

import de.heiden.c64dt.assembler.command.CommandBuffer;
import de.heiden.c64dt.assembler.command.ICommand;
import de.heiden.c64dt.assembler.command.OpcodeCommand;
import de.heiden.c64dt.assembler.gui.CodeTableModel;
import de.heiden.c64dt.assembler.gui.util.TableUtil;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * All types of "go to" actions
 */
public class GotoActions {
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
  public GotoActions(final JTable table) {
    this.table = table;

    gotoAction = createGotoAction();

    table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent listSelectionEvent) {
        gotoAction.setEnabled(table.getSelectedRowCount() == 1);
      }
    });

    table.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
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
  public void addToMenu(JPopupMenu menu) {
    menu.add(gotoAction);
  }

  /**
   * Create action "Go to".
   */
  private Action createGotoAction() {
    return new AbstractAction("Go to") {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        gotoDestination(table.getSelectedRow());
      }
    };
  }

  /**
   * Goto destination (address) of an opcode.
   *
   * @param row Row in which the event has been triggered or -1
   */
  private void gotoDestination(int row) {
    if (row < 0) {
      // now row -> no jump
      return;
    }

    CodeTableModel model = (CodeTableModel) table.getModel();
    CommandBuffer commands = model.getReassembler().getCommands();

    int index = model.getIndex(row);

    // just works for opcodes
    ICommand command = commands.getCommand(index);
    if (!(command instanceof OpcodeCommand)) {
      return;
    }

    // just works for opcodes with an address
    OpcodeCommand opcodeCommand = (OpcodeCommand) command;
    if (!opcodeCommand.isArgumentAddress()) {
      return;
    }

    // check if address is known
    int address = opcodeCommand.getArgumentAddress();
    if (!commands.hasAddress(address)) {
      return;
    }

    // do the jump
    int jumpIndex = commands.indexForAddress(address);
    if (jumpIndex < 0) {
      return;
    }

    gotoIndex(jumpIndex);
  }

  /**
   * Jump to the given relative address.
   *
   * @param index Relative address
   */
  public void gotoIndex(int index) {
    CodeTableModel model = (CodeTableModel) table.getModel();

    if (!model.getReassembler().getCommands().hasIndex(index)) {
      return;
    }

    int row = model.getRow(index);
    table.scrollRectToVisible(TableUtil.getRowBounds(table, row, 3));
    table.getSelectionModel().setSelectionInterval(row, row);
  }
}

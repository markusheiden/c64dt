package de.heiden.c64dt.reassembler.gui.action;

import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.reassembler.Reassembler;
import de.heiden.c64dt.reassembler.command.CommandBuffer;
import de.heiden.c64dt.reassembler.command.ICommand;
import de.heiden.c64dt.reassembler.gui.CodeTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * "Mark as" actions.
 */
public class CodeTypeActions {
  /**
   * The table.
   */
  private final JTable table;

  /**
   * Action "Mark as unknown".
   */
  private final Action markAsUnknownAction;

  /**
   * Action "Mark as code".
   */
  private final Action markAsCodeAction;

  /**
   * Action "Mark as data".
   */
  private final Action markAsDataAction;

  /**
   * Action "Mark as absolute address".
   */
  private final Action markAsAbsoluteAddressAction;

  /**
   * Constructor.
   *
   * @param table The table this action works on
   */
  public CodeTypeActions(final JTable table) {
    this.table = table;

    markAsUnknownAction = createMarkAsUnknownAction();
    markAsCodeAction = createMarkAsCodeAction();
    markAsDataAction = createMarkAsDataAction();
    markAsAbsoluteAddressAction = createMarkAsAbsoluteAddressAction();

    table.getSelectionModel().addListSelectionListener(listSelectionEvent -> {
      boolean isSelection = table.getSelectedRowCount() > 0;
      markAsUnknownAction.setEnabled(isSelection);
      markAsCodeAction.setEnabled(isSelection);
      markAsDataAction.setEnabled(isSelection);
      markAsAbsoluteAddressAction.setEnabled(isSelection);
    });
  }

  /**
   * Add the actions to a menu.
   *
   * @param menu Menu
   */
  public void addToMenu(JPopupMenu menu) {
    menu.add(markAsUnknownAction);
    menu.add(markAsCodeAction);
    menu.add(markAsDataAction);
    menu.add(markAsAbsoluteAddressAction);
  }

  /**
   * Create action "Mark as unknown".
   */
  private Action createMarkAsUnknownAction() {
    return new AbstractAction("Mark as unknown") {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        mark(CodeType.UNKNOWN);
      }
    };
  }

  /**
   * Create action "Mark as code".
   */
  private Action createMarkAsCodeAction() {
    return new AbstractAction("Mark as code") {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        mark(CodeType.CODE);
      }
    };
  }

  /**
   * Create action "Mark as data".
   */
  private Action createMarkAsDataAction() {
    return new AbstractAction("Mark as data") {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        mark(CodeType.DATA);
      }
    };
  }

  /**
   * Create action "Mark as absolute address".
   */
  private Action createMarkAsAbsoluteAddressAction() {
    return new AbstractAction("Mark as absolute address") {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        mark(CodeType.ADDRESS);
      }
    };
  }

  /**
   * Marks the code with the given code type.
   *
   * @param type Code type
   */
  private void mark(CodeType type) {
    CodeTableModel model = (CodeTableModel) table.getModel();
    Reassembler reassembler = model.getReassembler();
    CommandBuffer commands = reassembler.getCommands();
    for (int row : table.getSelectedRows()) {
      int index = model.getIndex(row);
      ICommand command = commands.getCommand(index);
      // TODO mh: do not overwrite OPCODE etc.?
      commands.setType(index, index + command.getSize(), type);
    }

    reassembler.reassemble();
    model.update();
  }
}

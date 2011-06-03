package de.heiden.c64dt.assembler.gui.action;

import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.assembler.command.CommandBuffer;
import de.heiden.c64dt.assembler.command.ICommand;
import de.heiden.c64dt.assembler.gui.CodeTableModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;

/**
 * "Mark as" actions.
 */
public class CodeTypeActions
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
  public CodeTypeActions(JTable table)
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
    final Action markAsUnknownAction = markAsUnknown();
    final Action markAsCodeAction = markAsCode();
    final Action markAsDataAction = markAsData();
    final Action markAsAbsoluteAddressAction = markAsAbsoluteAddress();

    menu.add(markAsUnknownAction);
    menu.add(markAsCodeAction);
    menu.add(markAsDataAction);
    menu.add(markAsAbsoluteAddressAction);

    table.getSelectionModel().addListSelectionListener(new ListSelectionListener()
    {
      @Override
      public void valueChanged(ListSelectionEvent listSelectionEvent)
      {
        boolean isSelection = table.getSelectedRowCount() > 0;
        markAsUnknownAction.setEnabled(isSelection);
        markAsCodeAction.setEnabled(isSelection);
        markAsDataAction.setEnabled(isSelection);
        markAsAbsoluteAddressAction.setEnabled(isSelection);
      }
    });
  }

  /**
   * Create action "Mark as unknown".
   */
  public Action markAsUnknown()
  {
    return new AbstractAction("Mark as unknown")
    {
      @Override
      public void actionPerformed(ActionEvent actionEvent)
      {
        mark(CodeType.UNKNOWN);
      }
    };
  }

  /**
   * Create action "Mark as code".
   */
  public Action markAsCode()
  {
    return new AbstractAction("Mark as code")
    {
      @Override
      public void actionPerformed(ActionEvent actionEvent)
      {
        mark(CodeType.CODE);
      }
    };
  }

  /**
   * Create action "Mark as data".
   */
  public Action markAsData()
  {
    return new AbstractAction("Mark as data")
    {
      @Override
      public void actionPerformed(ActionEvent actionEvent)
      {
        mark(CodeType.DATA);
      }
    };
  }

  /**
   * Create action "Mark as absolute address".
   */
  public Action markAsAbsoluteAddress()
  {
    return new AbstractAction("Mark as absolute address")
    {
      @Override
      public void actionPerformed(ActionEvent actionEvent)
      {
        mark(CodeType.ABSOLUTE_ADDRESS);
      }
    };
  }

  /**
   * Marks the code with the given code type.
   *
   * @param type Code type
   */
  private void mark(CodeType type)
  {
    CodeTableModel model = (CodeTableModel) table.getModel();
    CommandBuffer commands = model.getReassembler().getCommands();
    for (int row : table.getSelectedRows())
    {
      int index = model.getIndex(row);
      ICommand command = commands.getCommand(index);
      // TODO mh: do not overwrite OPCODE etc.?
      commands.setType(index, index + command.getSize(), type);
    }

    model.update();
  }
}

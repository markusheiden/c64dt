package de.heiden.c64dt.assembler.gui.action;

import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.assembler.command.CommandBuffer;
import de.heiden.c64dt.assembler.command.ICommand;
import de.heiden.c64dt.assembler.gui.CodeTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * "Mark as" actions.
 */
public class CodeTypeActions
{
  /**
   * Add the actions to a menu.
   *
   * @param menu Menu
   * @param table The table this action works on
   */
  public static void addToMenu(JPopupMenu menu, final JTable table)
  {
    if (table.getSelectedRowCount() == 0)
    {
      // No selection -> no actions possible
      return;
    }

    menu.add(markAsUnknown(table));
    menu.add(markAsCode(table));
    menu.add(markAsData(table));
    menu.add(markAsAbsoluteAddress(table));
  }

  /**
   * Create action "Mark as unknown".
   *
   * @param table The table this action works on
   */
  public static Action markAsUnknown(final JTable table)
  {
    return new AbstractAction("Mark as unknown")
    {
      @Override
      public void actionPerformed(ActionEvent actionEvent)
      {
        mark(table, CodeType.UNKNOWN);
      }
    };
  }

  /**
   * Create action "Mark as code".
   *
   * @param table The table this action works on
   */
  public static Action markAsCode(final JTable table)
  {
    return new AbstractAction("Mark as code")
    {
      @Override
      public void actionPerformed(ActionEvent actionEvent)
      {
        mark(table, CodeType.CODE);
      }
    };
  }

  /**
   * Create action "Mark as data".
   *
   * @param table The table this action works on
   */
  public static Action markAsData(final JTable table)
  {
    return new AbstractAction("Mark as data")
    {
      @Override
      public void actionPerformed(ActionEvent actionEvent)
      {
        mark(table, CodeType.DATA);
      }
    };
  }

  /**
   * Create action "Mark as absolute address".
   *
   * @param table The table this action works on
   */
  public static Action markAsAbsoluteAddress(final JTable table)
  {
    return new AbstractAction("Mark as absolute address")
    {
      @Override
      public void actionPerformed(ActionEvent actionEvent)
      {
        mark(table, CodeType.ABSOLUTE_ADDRESS);
      }
    };
  }

  /**
   * Marks the code with the given code type.
   *
   * @param table The table
   * @param type Code type
   */
  private static void mark(JTable table, CodeType type)
  {
    CodeTableModel model = (CodeTableModel) table.getModel();
    CommandBuffer commands = model.getCommands();
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

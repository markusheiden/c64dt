package de.heiden.c64dt.assembler.gui.action;

import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.assembler.command.CommandBuffer;
import de.heiden.c64dt.assembler.command.ICommand;
import de.heiden.c64dt.assembler.gui.CodeTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Mark as code action.
 */
public class CodeTypeActions
{
  /**
   * Add the action to a menu.
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

    JMenuItem code = new JMenuItem("Mark as code");
    menu.add(code);
    code.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent actionEvent)
      {
        mark(table, CodeType.CODE);
      }
    });

    JMenuItem data = new JMenuItem("Mark as data");
    menu.add(data);
    data.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent actionEvent)
      {
        mark(table, CodeType.DATA);
      }
    });

    JMenuItem address = new JMenuItem("Mark as absolute address");
    menu.add(address);
    address.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent actionEvent)
      {
        mark(table, CodeType.ABSOLUTE_ADDRESS);
      }
    });
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

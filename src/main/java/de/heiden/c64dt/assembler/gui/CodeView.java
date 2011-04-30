package de.heiden.c64dt.assembler.gui;

import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.assembler.Reassembler;
import de.heiden.c64dt.assembler.command.CommandBuffer;
import de.heiden.c64dt.assembler.detector.JsrDetector;
import de.heiden.c64dt.assembler.gui.action.CodeTypeActions;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * View for reassembled code.
 */
public class CodeView
{
  /**
   * The reassembler.
   */
  private Reassembler reassembler;

  /**
   * Model.
   */
  private final CodeTableModel model;

  /**
   * Constructor.
   */
  public CodeView()
  {
    this.reassembler = new Reassembler();
    model = new CodeTableModel(reassembler);
  }

  /**
   * Use another reassembler.
   *
   * @param reassembler
   */
  public void use(Reassembler reassembler)
  {
    Assert.notNull(reassembler, "Precondition: reassembler != null");

    this.reassembler = reassembler;
    model.setReassembler(reassembler);
    model.update();
  }

  /**
   * Create GUI representation.
   */
  public JComponent createComponent()
  {
    final JTable table = new JTable(model);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
    table.getColumnModel().getColumn(0).setMaxWidth(40);
    table.getColumnModel().getColumn(1).setMaxWidth(40);
    table.getColumnModel().getColumn(2).setPreferredWidth(100);
    table.getColumnModel().getColumn(3).setMaxWidth(100);
    table.getColumnModel().getColumn(4).setPreferredWidth(200);
    table.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
    table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

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
        if (row >= 0)
        {
          table.getSelectionModel().addSelectionInterval(row, row);
        }

        // show (dynamic) context menu
        createContextMenu(table).show(table, e.getX(), e.getY());
      }
    });

    return new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
  }

  /**
   * Create context menu.
   *
   * @param table The table the action work on
   */
  private JPopupMenu createContextMenu(JTable table)
  {
    JPopupMenu contextMenu = new JPopupMenu();

    CodeTypeActions.addToMenu(contextMenu, table);

    return contextMenu;
  }

  /**
   * Reassemble the given code
   *
   * @param is Input stream with code
   * @throws IOException In case of IO errors
   */
  public void reassemble(InputStream is) throws IOException
  {
    reassembler.reassemble(is);
    model.update();
  }

  /**
   * Just for testing purposes: Reassemble fixed code at startup.
   */
  public void reassemble()
  {
    try
    {
      final String BASEDIR = "retro replay";
      final String ROM_NAME = "rr38q-cnet-%d.bin";

      File file = new File(BASEDIR, String.format(ROM_NAME, 0));
      System.out.println("Reassembling " + file.getCanonicalPath() + " (" + file.length() + " Bytes)");

      byte[] code = FileCopyUtils.copyToByteArray(new FileInputStream(file));

      JsrDetector jsr = new JsrDetector();
      reassembler.add(jsr);

      CommandBuffer commands = new CommandBuffer(code, 0x8000);
      commands.setType(0x0000, 0x0004, CodeType.ABSOLUTE_ADDRESS);
      commands.setType(0x0004, 0x0009, CodeType.DATA);
      commands.setType(0x0009, 0x0060, CodeType.OPCODE);
      commands.setType(0x0080, 0x017F, CodeType.DATA);
      commands.setType(0x021D, 0x022F, CodeType.DATA);
      commands.rebase(0x0E0D, 0xE000);
      commands.rebase(0x0FBE, 0x8000);
      // commands.base(0x1D9F, 0x0100);
      commands.rebase(0x1DB3, 0x8000);
      commands.rebase(0x1E00, 0x0C000);
      commands.setType(0x1E00, 0x1E15, CodeType.DATA);
      commands.addSubroutine(0x1F03, 2);
      commands.setType(0x1FF8, 0x2000, CodeType.ABSOLUTE_ADDRESS);

      reassembler.reassemble(commands);

      model.update();
    }
    catch (IOException e)
    {
      // this method is just for test, so just output the exception
      e.printStackTrace();
    }

  }
}

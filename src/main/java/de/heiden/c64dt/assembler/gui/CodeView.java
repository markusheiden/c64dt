package de.heiden.c64dt.assembler.gui;

import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.assembler.ReassemblerMapper;
import de.heiden.c64dt.assembler.ILabel;
import de.heiden.c64dt.assembler.Reassembler;
import de.heiden.c64dt.assembler.command.CommandBuffer;
import de.heiden.c64dt.assembler.command.ICommand;
import de.heiden.c64dt.assembler.detector.JsrDetector;
import org.springframework.util.FileCopyUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;

import static de.heiden.c64dt.util.HexUtil.hexBytePlain;
import static de.heiden.c64dt.util.HexUtil.hexWordPlain;

/**
 * View for reassembled code.
 */
public class CodeView
{
  /**
   * Model.
   */
  private final DefaultTableModel model;

  /**
   * Constructor.
   */
  public CodeView()
  {
    model = new DefaultTableModel(
      new String[]{"Flags", "Addr", "Bytes", "Label", "Code"}, 0);
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
    table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

    table.addMouseListener(new MouseAdapter()
    {
      @Override
      public void mouseReleased(MouseEvent e)
      {
        if ((e.getButton() & MouseEvent.BUTTON2) == 0) {
          // not right mouse button -> ignore
          return;
        }

        createContextMenu().show(table, e.getX(), e.getY());
      }
    });

    return new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
  }

  /**
   * Create context menu.
   */
  private JPopupMenu createContextMenu() {
    JPopupMenu contextMenu = new JPopupMenu();
    contextMenu.add(new JMenuItem("Test"));

    return contextMenu;
  }

  public void reassemble() {
    try
    {
      model.setRowCount(0);

      StringBuilder builder = new StringBuilder();

      Reassembler reassembler = init();
      CommandBuffer commands = reassembler.getCommands();
      commands.restart();
      while (commands.hasNextCommand()) {
        int index = commands.getCurrentIndex();
        int addr = commands.addressForIndex(index);
        ICommand command = commands.nextCommand();

        builder.setLength(0);
        if (!command.isReachable())
        {
          builder.append("U");
        }
        for (int i = 1; i < command.getSize(); i++)
        {
          if (commands.hasCodeLabel(addr + i))
          {
            builder.append("C");
          }
          if (commands.hasDataLabel(addr + i))
          {
            builder.append("D");
          }
        }
        String flags = builder.toString();

        builder.setLength(0);
        java.util.List<Integer> data = command.toBytes();
        for (int dataByte : data)
        {
          builder.append(" ");
          builder.append(hexBytePlain(dataByte));
        }
        String bytes = builder.toString().trim();

        builder.setLength(0);
        ILabel label = commands.getLabel();
        if (label != null)
        {
          // TODO mh: check length of label?
          builder.append(label.toString()).append(":");
        }
        String labelString = builder.toString();

        builder.setLength(0);
        if (command != null)
        {
          builder.append(command.toString(commands));
        }
        else
        {
          // TODO mh: log error?
          builder.append("???");
        }
        String code = builder.toString();

        model.addRow(new Object[]{flags, hexWordPlain(addr), bytes, labelString, code});
      }

      new ReassemblerMapper().write(reassembler);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  private static final String BASEDIR = "retro replay";
  private static final String ROM_NAME = "rr38q-cnet-%d.bin";

  private Reassembler init() throws IOException
  {
    File file = new File(BASEDIR, String.format(ROM_NAME, 0));
    System.out.println("Reassembling " + file.getCanonicalPath() + " (" + file.length() + " Bytes)");

    byte[] code = FileCopyUtils.copyToByteArray(new FileInputStream(file));

    Reassembler reassembler = new Reassembler();
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
    jsr.addSubroutine(0x9F03, 2);
    commands.setType(0x1FF8, 0x2000, CodeType.ABSOLUTE_ADDRESS);

    reassembler.reassemble(commands);

    return reassembler;
  }
}

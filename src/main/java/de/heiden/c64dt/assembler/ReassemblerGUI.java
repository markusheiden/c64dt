package de.heiden.c64dt.assembler;

import de.heiden.c64dt.assembler.command.CommandBuffer;
import de.heiden.c64dt.assembler.detector.JsrDetector;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * GUI for {@link Reassembler}.
 */
public class ReassemblerGUI extends JFrame
{
  public ReassemblerGUI() throws HeadlessException
  {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    setLayout(new BorderLayout());

    //
    //
    //

    final JList list = new JList();
    list.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));

    JScrollPane scroll = new JScrollPane(list,
      JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

    add(scroll, BorderLayout.CENTER);

    //
    // Menu bar
    //

    JMenuBar menuBar = new JMenuBar();
    add(menuBar, BorderLayout.NORTH);

    JButton start = new JButton();
    start.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent actionEvent)
      {
        try
        {
          String model = init();
          String[] lines = StringUtils.tokenizeToStringArray(model, "\n", false, false);
          list.setListData(lines);
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
    });
    menuBar.add(start);

    pack();
  }

  private static final String BASEDIR = "retro replay";
  private static final String ROM_NAME = "rr38q-cnet-%d.bin";

  private String init() throws IOException
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

    StringWriter writer = new StringWriter(256 * 1024);
    reassembler.reassemble(code, commands, writer);

    writer.flush();
    return writer.toString();
  }

  public static void main(String[] args)
  {
    ReassemblerGUI gui = new ReassemblerGUI();
    gui.setVisible(true);
  }
}

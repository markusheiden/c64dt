package de.heiden.c64dt.assembler;

import de.heiden.c64dt.assembler.command.CommandBuffer;
import de.heiden.c64dt.assembler.command.ICommand;
import de.heiden.c64dt.assembler.detector.JsrDetector;
import de.heiden.c64dt.assembler.gui.CodeView;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import static de.heiden.c64dt.util.HexUtil.hexBytePlain;
import static de.heiden.c64dt.util.HexUtil.hexWordPlain;

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
    // Reassembled code
    //

    final CodeView code = new CodeView();
    add(code.createComponent(), BorderLayout.CENTER);

    //
    // Menu bar
    //

    JMenuBar menuBar = new JMenuBar();
    add(menuBar, BorderLayout.NORTH);

    JButton start = new JButton("Reassemble");
    start.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent actionEvent)
      {
        code.reassemble();
      }
    });
    menuBar.add(start);

    pack();
  }

  public static void main(String[] args)
  {
    ReassemblerGUI gui = new ReassemblerGUI();
    gui.setVisible(true);
  }
}

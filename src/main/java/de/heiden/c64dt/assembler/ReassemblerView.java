package de.heiden.c64dt.assembler;

import de.heiden.c64dt.assembler.gui.CodeView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * GUI for {@link Reassembler}.
 */
public class ReassemblerView extends JFrame
{
  private final CodeView code;

  /**
   * Constructor.
   */
  public ReassemblerView()
  {
    setTitle("C64 Reassembler");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    setLayout(new BorderLayout());

    //
    // Reassembled code
    //

    code = new CodeView();
    add(code.createComponent(), BorderLayout.CENTER);

    //
    // Menu bar
    //

    add(createMenu(), BorderLayout.NORTH);

    pack();
  }

  /**
   * Create menu bar.
   */
  private JMenuBar createMenu() {
    JMenuBar menuBar = new JMenuBar();

    JMenu file = new JMenu("File");
    menuBar.add(file);

    JMenuItem fileOpen = new JMenuItem("Open...");
    file.add(fileOpen);
    fileOpen.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent actionEvent)
      {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(ReassemblerView.this);
      }
    });

    // for testing purposes only...
    JMenuItem fileReassemble = new JMenuItem("Reassemble");
    file.add(fileReassemble);
    fileReassemble.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent actionEvent)
      {
        code.reassemble();
      }
    });


    return menuBar;
  }
}

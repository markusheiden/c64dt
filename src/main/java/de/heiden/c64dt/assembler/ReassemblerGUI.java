package de.heiden.c64dt.assembler;

import de.heiden.c64dt.assembler.gui.CodeView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * GUI for {@link Reassembler}.
 */
public class ReassemblerGUI extends JFrame
{
  private final CodeView code;

  /**
   * Constructor.
   */
  public ReassemblerGUI()
  {
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

    return menuBar;
  }

  /**
   * Start reassembler gui.
   *
   * @param args arguments (currently not evaluated)
   */
  public static void main(String[] args)
  {
    ReassemblerGUI gui = new ReassemblerGUI();
    gui.setVisible(true);
  }
}

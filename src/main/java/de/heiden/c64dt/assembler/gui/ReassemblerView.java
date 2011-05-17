package de.heiden.c64dt.assembler.gui;

import de.heiden.c64dt.assembler.Reassembler;
import de.heiden.c64dt.assembler.ReassemblerMapper;
import org.apache.log4j.Logger;
import org.exolab.castor.xml.Marshaller;
import org.springframework.util.Assert;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * GUI for {@link de.heiden.c64dt.assembler.Reassembler}.
 */
public class ReassemblerView extends JFrame
{
  private final Logger logger = Logger.getLogger(getClass());

  private Reassembler reassembler;

  private File currentFile;

  /**
   * The code view.
   */
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

    // for testing purposes only...
    code.reassemble();

    //
    // Menu bar
    //

    setJMenuBar(createMenu());

    pack();
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
    code.use(reassembler);
  }

  /**
   * Create menu bar.
   */
  private JMenuBar createMenu()
  {
    JMenuBar menuBar = new JMenuBar();

    JMenu file = new JMenu("File");
    menuBar.add(file);

    JMenuItem fileOpen = new JMenuItem("Reassemble...");
    file.add(fileOpen);
    fileOpen.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent actionEvent)
      {
        try
        {
          JFileChooser chooser = new JFileChooser();
          int result = chooser.showOpenDialog(ReassemblerView.this);
          if (result == JFileChooser.APPROVE_OPTION)
          {
            code.reassemble(new FileInputStream(chooser.getSelectedFile()));
          }
        }
        catch (IOException e)
        {
          logger.error("Failed to open file", e);
          JOptionPane.showMessageDialog(ReassemblerView.this,
            "Failed to open file:\n" + e.getMessage(), "File error", JOptionPane.ERROR_MESSAGE);
        }
      }
    });

    JMenuItem fileLoad = new JMenuItem("Load...");
    file.add(fileLoad);
    fileLoad.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent actionEvent)
      {
        try
        {
          JFileChooser chooser = new JFileChooser();
          int result = chooser.showOpenDialog(ReassemblerView.this);
          if (result == JFileChooser.APPROVE_OPTION)
          {
            currentFile = chooser.getSelectedFile();
            code.use(new ReassemblerMapper().read(new FileInputStream(currentFile)));
//            fileSave.setEnabled(true);
          }
        }
        catch (Exception e)
        {
          logger.error("Failed to open file", e);
          currentFile = null;
          JOptionPane.showMessageDialog(ReassemblerView.this,
            "Failed to open file:\n" + e.getMessage(), "File error", JOptionPane.ERROR_MESSAGE);
        }
      }
    });

    JMenuItem fileSave = new JMenuItem("Save");
    file.add(fileSave);
    fileSave.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent actionEvent)
      {
        try
        {
          new ReassemblerMapper().write(reassembler, new FileOutputStream(currentFile));
        }
        catch (Exception e)
        {
          logger.error("Failed to save file", e);
          JOptionPane.showMessageDialog(ReassemblerView.this,
            "Failed to save file:\n" + e.getMessage(), "File error", JOptionPane.ERROR_MESSAGE);
        }
      }
    });

    JMenuItem fileSaveAs = new JMenuItem("Save as...");
    file.add(fileSaveAs);
    fileSaveAs.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent actionEvent)
      {
        try
        {
          JFileChooser chooser = new JFileChooser();
          int result = chooser.showSaveDialog(ReassemblerView.this);
          if (result == JFileChooser.APPROVE_OPTION)
          {
//            Writer writer = new FileWriter("test.xml");
//            Marshaller.marshal(reassembler, writer);
            new ReassemblerMapper().write(reassembler, new FileOutputStream(chooser.getSelectedFile()));
          }
        }
        catch (Exception e)
        {
          logger.error("Failed to save file", e);
          JOptionPane.showMessageDialog(ReassemblerView.this,
            "Failed to save file:\n" + e.getMessage(), "File error", JOptionPane.ERROR_MESSAGE);
        }
      }
    });

    return menuBar;
  }
}

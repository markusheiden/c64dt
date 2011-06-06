package de.heiden.c64dt.assembler.gui;

import de.heiden.c64dt.assembler.Reassembler;
import de.heiden.c64dt.assembler.ReassemblerMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * GUI for {@link de.heiden.c64dt.assembler.Reassembler}.
 */
@Component
public class ReassemblerView extends JFrame
{
  private final Logger logger = Logger.getLogger(getClass());

  private Reassembler reassembler;

  private File currentFile;

  private JMenuItem fileOpen;
  private JMenuItem fileLoad;
  private JMenuItem fileSave;
  private JMenuItem fileSaveAs;

  /**
   * The code view.
   */
  @Autowired
  private CodeView code;

  /**
   * Cross reference.
   */
  @Autowired
  private CrossReferenceView crossReference;

  /**
   * Constructor.
   */
  public ReassemblerView()
  {
    setTitle("C64 Reassembler");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

  @PostConstruct
  private void init()
  {
    JSplitPane main = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    add(main);

    //
    // Reassembled code
    //

    main.setRightComponent(code.createComponent());

    //
    // Helper views
    //

    JPanel helperPane = new JPanel();
    helperPane.setLayout(new GridLayout(2, 1));
    main.setLeftComponent(helperPane);

    helperPane.add(new JPanel());

    helperPane.add(crossReference.createComponent());

    //
    // Menu bar
    //

    setJMenuBar(createMenu());

    pack();

    use(new Reassembler());
    // for testing purposes only...
    reassemble();

    updateGUI();
  }

  /**
   * Just for testing purposes: Reassemble fixed code at startup.
   */
  private void reassemble()
  {
    try
    {
      File file = new File("retro replay", "rr38q-cnet-0.xml");
      use(new ReassemblerMapper().read(new FileInputStream(file)));
      currentFile = file;
    }
    catch (Exception e)
    {
      // this method is just for test, so just output the exception
      e.printStackTrace();
    }
  }

  /**
   * Use another reassembler.
   *
   * @param reassembler Reassembler
   */
  public void use(Reassembler reassembler)
  {
    Assert.notNull(reassembler, "Precondition: reassembler != null");

    this.currentFile = null;
    this.reassembler = reassembler;

    code.use(reassembler);
    crossReference.use(reassembler);
  }

  /**
   * Update GUI state.
   */
  protected final void updateGUI()
  {
    String title = "C64 Reassembler";
    if (currentFile != null)
    {
      title = currentFile.getAbsolutePath() + " - " + title;
    }
    setTitle(title);

    fileSave.setEnabled(currentFile != null);
  }

  //
  // Menu
  //

  /**
   * Create menu bar.
   */
  private JMenuBar createMenu()
  {
    JMenuBar menuBar = new JMenuBar();

    JMenu file = new JMenu("File");
    menuBar.add(file);

    fileOpen = new JMenuItem("Reassemble...");
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
            // TODO mh: ask for start address
            currentFile = null;
            reassembler.reassemble(new FileInputStream(chooser.getSelectedFile()));
          }
        }
        catch (IOException e)
        {
          logger.error("Failed to open file", e);
          use(new Reassembler());
          JOptionPane.showMessageDialog(ReassemblerView.this,
            "Failed to open file:\n" + e.getMessage(), "File error", JOptionPane.ERROR_MESSAGE);
        }
        updateGUI();
      }
    });

    fileLoad = new JMenuItem("Load...");
    file.add(fileLoad);
    fileLoad.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent actionEvent)
      {
        try
        {
          JFileChooser chooser = new JFileChooser();
          if (currentFile != null)
          {
            chooser.setCurrentDirectory(currentFile.getParentFile());
          }
          int result = chooser.showOpenDialog(ReassemblerView.this);
          if (result == JFileChooser.APPROVE_OPTION)
          {
            use(new ReassemblerMapper().read(new FileInputStream(chooser.getSelectedFile())));
            currentFile = chooser.getSelectedFile();
          }
        }
        catch (Exception e)
        {
          logger.error("Failed to open file", e);
          use(new Reassembler());
          JOptionPane.showMessageDialog(ReassemblerView.this,
            "Failed to open file:\n" + e.getMessage(), "File error", JOptionPane.ERROR_MESSAGE);
        }
        updateGUI();
      }
    });

    fileSave = new JMenuItem("Save");
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
        updateGUI();
      }
    });

    fileSaveAs = new JMenuItem("Save as...");
    file.add(fileSaveAs);
    fileSaveAs.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent actionEvent)
      {
        try
        {
          JFileChooser chooser = new JFileChooser();
          if (currentFile != null)
          {
            chooser.setCurrentDirectory(currentFile.getParentFile());
            chooser.setSelectedFile(currentFile);
          }
          int result = chooser.showSaveDialog(ReassemblerView.this);
          if (result == JFileChooser.APPROVE_OPTION)
          {
            new ReassemblerMapper().write(reassembler, new FileOutputStream(chooser.getSelectedFile()));
            currentFile = chooser.getSelectedFile();
          }
        }
        catch (Exception e)
        {
          logger.error("Failed to save file", e);
          JOptionPane.showMessageDialog(ReassemblerView.this,
            "Failed to save file:\n" + e.getMessage(), "File error", JOptionPane.ERROR_MESSAGE);
        }
        updateGUI();
      }
    });

    return menuBar;
  }
}

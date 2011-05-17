package de.heiden.c64dt.assembler.gui;

import de.heiden.c64dt.assembler.CodeType;
import de.heiden.c64dt.assembler.Reassembler;
import de.heiden.c64dt.assembler.ReassemblerMapper;
import de.heiden.c64dt.assembler.command.CommandBuffer;
import de.heiden.c64dt.assembler.detector.JsrDetector;
import org.apache.log4j.Logger;
import org.exolab.castor.xml.Marshaller;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
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
    reassemble();

    //
    // Menu bar
    //

    setJMenuBar(createMenu());

    pack();
  }

  /**
   * Just for testing purposes: Reassemble fixed code at startup.
   */
  private void reassemble()
  {
    reassembler = new Reassembler();

    try
    {
      final String BASEDIR = "retro replay";
      final String ROM_NAME = "rr38q-cnet-%d.bin";

      File file = new File(BASEDIR, String.format(ROM_NAME, 0));
      System.out.println("Reassembling " + file.getCanonicalPath() + " (" + file.length() + " Bytes)");

      byte[] bytes = FileCopyUtils.copyToByteArray(new FileInputStream(file));

      JsrDetector jsr = new JsrDetector();
      reassembler.add(jsr);

      CommandBuffer commands = new CommandBuffer(bytes, 0x8000);
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

      code.use(reassembler);
    }
    catch (IOException e)
    {
      // this method is just for test, so just output the exception
      e.printStackTrace();
    }
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
    code.use(reassembler);
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
            reassemble(new FileInputStream(chooser.getSelectedFile()));
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
            Writer writer = new FileWriter("test.xml");
            Marshaller.marshal(reassembler, writer);
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

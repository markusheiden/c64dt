package de.heiden.c64dt.reassembler.gui;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import jakarta.annotation.PostConstruct;
import javax.swing.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.heiden.c64dt.assembler.CodeBuffer;
import de.heiden.c64dt.reassembler.Reassembler;
import de.heiden.c64dt.reassembler.xml.XmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.heiden.c64dt.common.Requirements.R;

/**
 * GUI for {@link Reassembler}.
 */
@Component
public class ReassemblerView extends JFrame {
  /**
   * Logger.
   */
  private final Logger logger = LoggerFactory.getLogger(getClass());

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
  public ReassemblerView() {
    setTitle("C64 Reassembler");
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  }

  @PostConstruct
  private void init() {
    JSplitPane main = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    main.setContinuousLayout(true);
    main.setOneTouchExpandable(true);
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
    setExtendedState(JFrame.MAXIMIZED_BOTH);

    use(new Reassembler());
    // for testing purposes only...
    reassemble();

    updateGUI();
  }

  /**
   * Just for testing purposes: Reassemble fixed code at startup.
   */
  private void reassemble() {
    try {
      File file = new File("retro replay", "rr38q-cnet-0.xml");
      Reassembler reassembler = XmlUtil.unmarshal(new FileInputStream(file), Reassembler.class);
      use(reassembler);
      currentFile = file;
    } catch (Exception e) {
      // this method is just for test, so just output the exception
      e.printStackTrace();
    }
  }

  /**
   * Use another reassembler.
   *
   * @param reassembler Reassembler
   */
  public void use(Reassembler reassembler) {
    R.requireThat(reassembler, "reassembler").isNotNull();

    this.currentFile = null;
    this.reassembler = reassembler;

    code.use(reassembler);
    crossReference.use(reassembler);
  }

  /**
   * Update GUI state.
   */
  protected final void updateGUI() {
    String title = "C64 Reassembler";
    if (currentFile != null) {
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
  private JMenuBar createMenu() {
    JMenuBar menuBar = new JMenuBar();

    JMenu file = new JMenu("File");
    menuBar.add(file);

    fileOpen = new JMenuItem("Reassemble...");
    file.add(fileOpen);
    fileOpen.addActionListener(actionEvent -> {
      try {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(ReassemblerView.this);
        if (result == JFileChooser.APPROVE_OPTION) {
          // TODO mh: ask for start address
          currentFile = null;
          reassembler.reassemble(CodeBuffer.fromCode(0x8000, new FileInputStream(chooser.getSelectedFile())));
        }
      } catch (IOException e) {
        logger.error("Failed to open file", e);
        use(new Reassembler());
        JOptionPane.showMessageDialog(ReassemblerView.this,
          "Failed to open file:\n" + e.getMessage(), "File error", JOptionPane.ERROR_MESSAGE);
      }
      updateGUI();
    });

    fileLoad = new JMenuItem("Load...");
    file.add(fileLoad);
    fileLoad.addActionListener(actionEvent -> {
      try {
        JFileChooser chooser = new JFileChooser();
        if (currentFile != null) {
          chooser.setCurrentDirectory(currentFile.getParentFile());
        }
        int result = chooser.showOpenDialog(ReassemblerView.this);
        if (result == JFileChooser.APPROVE_OPTION) {
          use(XmlUtil.unmarshal(new FileInputStream(chooser.getSelectedFile()), Reassembler.class));
          currentFile = chooser.getSelectedFile();
        }
      } catch (Exception e) {
        logger.error("Failed to open file", e);
        use(new Reassembler());
        JOptionPane.showMessageDialog(ReassemblerView.this,
          "Failed to open file:\n" + e.getMessage(), "File error", JOptionPane.ERROR_MESSAGE);
      }
      updateGUI();
    });

    fileSave = new JMenuItem("Save");
    file.add(fileSave);
    fileSave.addActionListener(actionEvent -> {
      try {
        XmlUtil.marshal(reassembler, new FileOutputStream(currentFile));
      } catch (Exception e) {
        logger.error("Failed to save file", e);
        JOptionPane.showMessageDialog(ReassemblerView.this,
          "Failed to save file:\n" + e.getMessage(), "File error", JOptionPane.ERROR_MESSAGE);
      }
      updateGUI();
    });

    fileSaveAs = new JMenuItem("Save as...");
    file.add(fileSaveAs);
    fileSaveAs.addActionListener(actionEvent -> {
      try {
        JFileChooser chooser = new JFileChooser();
        if (currentFile != null) {
          chooser.setCurrentDirectory(currentFile.getParentFile());
          chooser.setSelectedFile(currentFile);
        }
        int result = chooser.showSaveDialog(ReassemblerView.this);
        if (result == JFileChooser.APPROVE_OPTION) {
          XmlUtil.marshal(reassembler, new FileOutputStream(chooser.getSelectedFile()));
          currentFile = chooser.getSelectedFile();
        }
      } catch (Exception e) {
        logger.error("Failed to save file", e);
        JOptionPane.showMessageDialog(ReassemblerView.this,
          "Failed to save file:\n" + e.getMessage(), "File error", JOptionPane.ERROR_MESSAGE);
      }
      updateGUI();
    });

    return menuBar;
  }
}

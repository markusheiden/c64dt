package de.heiden.c64dt.gui.swing.browser;

import java.awt.*;

import javax.swing.*;

import de.heiden.c64dt.disk.IDiskImage;

/**
 * GUI of the browser.
 */
public class BrowserGUI extends JPanel {
  @SuppressWarnings("NullAway.Init")
  private JDirectory directory;
  @SuppressWarnings("NullAway.Init")
  private JBam bam;

  public BrowserGUI() {
    setLayout(new BorderLayout());

    add(explorer(), BorderLayout.WEST);
    add(directory(), BorderLayout.CENTER);
    add(details(), BorderLayout.EAST);
    add(new JPanel(), BorderLayout.SOUTH);
  }

  private Component explorer() {
    var result = new JPanel();
    result.setLayout(new BorderLayout());

    var list = new JList<String>(new String[]{"1", "2", "3"});
    result.add(list, BorderLayout.CENTER);

    return result;
  }

  private Component directory() {
    var result = new JPanel();
    result.setLayout(new BorderLayout());

    directory = new JDirectory();
    result.add(directory, BorderLayout.CENTER);

    return result;
  }

  private Component details() {
    var result = new JPanel();
    result.setLayout(new BorderLayout());

    bam = new JBam();
    result.add(bam, BorderLayout.CENTER);

    return result;
  }

  public void setDiskImage(IDiskImage diskImage) {
    bam.setDiskImage(diskImage);
    directory.setDirectory(diskImage.getDirectory());
  }
}

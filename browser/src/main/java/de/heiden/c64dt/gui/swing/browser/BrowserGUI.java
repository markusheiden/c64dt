package de.heiden.c64dt.gui.swing.browser;

import de.heiden.c64dt.disk.IDiskImage;

import javax.swing.*;
import java.awt.*;

import static org.bitbucket.cowwoc.requirements.core.Requirements.requireThat;

/**
 * GUI of the browser.
 */
public class BrowserGUI extends JPanel {
  private JDirectory directory;
  private JBam bam;

  public BrowserGUI() {
    setLayout(new BorderLayout());

    add(explorer(), BorderLayout.WEST);
    add(directory(), BorderLayout.CENTER);
    add(details(), BorderLayout.EAST);
    add(new JPanel(), BorderLayout.SOUTH);
  }

  private Component explorer() {
    JPanel result = new JPanel();
    result.setLayout(new BorderLayout());

    JList<String> list = new JList<>(new String[]{"1", "2", "3"});
    result.add(list, BorderLayout.CENTER);

    requireThat("result", result).isNotNull();
    return result;
  }

  private Component directory() {
    JPanel result = new JPanel();
    result.setLayout(new BorderLayout());

    directory = new JDirectory();
    result.add(directory, BorderLayout.CENTER);

    requireThat("result", result).isNotNull();
    return result;
  }

  private Component details() {
    JPanel result = new JPanel();
    result.setLayout(new BorderLayout());

    bam = new JBam();
    result.add(bam, BorderLayout.CENTER);

    requireThat("result", result).isNotNull();
    return result;
  }

  public void setDiskImage(IDiskImage diskImage) {
    requireThat("diskImage", diskImage).isNotNull();

    bam.setDiskImage(diskImage);
    directory.setDirectory(diskImage.getDirectory());
  }
}

package de.markusheiden.c64dt.browser;

import de.markusheiden.c64dt.disk.IDiskImage;
import org.springframework.util.Assert;

import javax.swing.*;
import java.awt.*;

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

    JList list = new JList(new String[]{"1", "2", "3"});
    result.add(list, BorderLayout.CENTER);

    Assert.notNull(result, "Postcondition: result != null");
    return result;
  }

  private Component directory() {
    JPanel result = new JPanel();
    result.setLayout(new BorderLayout());

    directory = new JDirectory();
    result.add(directory, BorderLayout.CENTER);

    Assert.notNull(result, "Postcondition: result != null");
    return result;
  }

  private Component details() {
    JPanel result = new JPanel();
    result.setLayout(new BorderLayout());

    bam = new JBam();
    result.add(bam, BorderLayout.CENTER);

    Assert.notNull(result, "Postcondition: result != null");
    return result;
  }

  public void setDiskImage(IDiskImage diskImage) {
    Assert.notNull(diskImage, "Precondition: diskImage != null");

    bam.setDiskImage(diskImage);
    directory.setDirectory(diskImage.getDirectory());
  }
}

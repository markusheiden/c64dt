package main.java.de.heiden.c64dt.browser;

import main.java.de.heiden.c64dt.disk.IBAM;
import main.java.de.heiden.c64dt.disk.IDiskImage;
import org.springframework.util.Assert;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Component for displaying a bam.
 */
public class JBam extends JComponent {
  private Color free;
  private Color used;
  private Color error;

  private IDiskImage diskImage;
  private int left;
  private int leftSpace;
  private int height;
  private int topSpace;
  private int box;
  private int border;

  /**
   * Constructor.
   */
  public JBam() {
    free = Color.GREEN;
    used = Color.BLUE;
    error = Color.RED;

    setOpaque(true);
  }

  /**
   * Set disk image (model).
   *
   * @param diskImage disk image
   */
  public void setDiskImage(IDiskImage diskImage) {
    Assert.notNull(diskImage, "Precondition: diskImage != null");

    this.diskImage = diskImage;

    Font font = getFont().deriveFont(Font.BOLD);
    setFont(font);
    FontMetrics fontMetrics = getFontMetrics(font);

    Rectangle bounds = fontMetrics.getStringBounds("88", getGraphics()).getBounds();
    left = (int) Math.ceil(bounds.getWidth());
    leftSpace = (int) Math.ceil(left / 2);
    height = (int) Math.ceil(bounds.getHeight());
    topSpace = (int) Math.ceil(height / 2);
    box = (int) Math.ceil(height / 2);
    border = box / 10;
    if (border == 0) {
      border = 1;
    }

    int x = leftSpace + left + border + diskImage.getTracks() * (box + border) + 1;
    int y = topSpace + height + border + diskImage.getSectors(1) * (box + border) + 1;
    setPreferredSize(new Dimension(x, y));

    repaint();
  }

  @Override
  protected void paintComponent(Graphics g) {
    Assert.notNull(g, "Precondition: g != null");

    IBAM bam = diskImage.getBAM();

    g.setColor(getBackground());
    g.fillRect(0, 0, getWidth(), getHeight());

    if (diskImage == null) {
      return;
    }

    FontMetrics fontMetrics = g.getFontMetrics();
    int descent = fontMetrics.getDescent();

    // track numbers
    g.setColor(getForeground());
    int x = leftSpace + left + border + box / 2; // center of first horizontal box
    int y = topSpace / 2 + height - descent; // base line of top space
    for (int track = 1; track < diskImage.getTracks(); track += 10) {
      String description = Integer.toString(track);
      // draw centered regarding to box
      Rectangle2D bounds = fontMetrics.getStringBounds(description, g);
      g.drawString(description, (int) Math.round(x - bounds.getWidth() / 2), y);
      x += 10 * (box + border);
    }

    // sector numbers
    g.setColor(getForeground());
    y = topSpace + height + border + box / 2; // center of first vertical box
    for (int sector = 0; sector < diskImage.getSectors(); sector += 10) {
      String description = Integer.toString(sector);
      // draw centered regarding to box
      Rectangle2D bounds = fontMetrics.getStringBounds(description, g);
      g.drawString(description, (int) Math.ceil(left - bounds.getWidth()) + (left - leftSpace) / 2, (int) Math.round(y + bounds.getHeight() / 2) - descent);
      y += 10 * (box + border);
    }

    // bam map
    x = leftSpace + left;
    for (int track = 1; track <= diskImage.getTracks(); track++) {
      y = topSpace + height;
      int spt = diskImage.getSectors(track);
      g.setColor(Color.BLACK);
      g.fillRect(x, y, border + box + border, border + spt * (box + border));
      for (int sector = 0; sector < spt; sector++) {
        Color color = used;
        if (diskImage.hasError(track, sector)) {
          color = error;
        } else if (bam.isFree(track, sector)) {
          color = free;
        }

        g.setColor(color);
        g.fillRect(x + border, y + border, box, box);
        y += box + border;
      }
      x += box + border;
    }
  }
}

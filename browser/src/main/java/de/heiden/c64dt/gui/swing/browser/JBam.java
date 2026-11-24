package de.heiden.c64dt.gui.swing.browser;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

import de.heiden.c64dt.disk.IBAM;
import de.heiden.c64dt.disk.IDiskImage;
import org.jspecify.annotations.Nullable;

/**
 * Component for displaying a BAM.
 */
public class JBam extends JComponent {
  private Color free;
  private Color used;
  private Color error;

  private @Nullable IDiskImage diskImage;
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
   * @param diskImage Disk image.
   */
  public void setDiskImage(IDiskImage diskImage) {
    this.diskImage = diskImage;

    var font = getFont().deriveFont(Font.BOLD);
    setFont(font);
    var fontMetrics = getFontMetrics(font);

    var bounds = fontMetrics.getStringBounds("88", getGraphics()).getBounds();
    left = (int) Math.ceil(bounds.getWidth());
    leftSpace = (int) Math.ceil(left / 2D);
    height = (int) Math.ceil(bounds.getHeight());
    topSpace = (int) Math.ceil(height / 2D);
    box = (int) Math.ceil(height / 2D);
    border = box / 10;
    if (border == 0) {
      border = 1;
    }

    var x = leftSpace + left + border + diskImage.getTracks() * (box + border) + 1;
    var y = topSpace + height + border + diskImage.getSectors(1) * (box + border) + 1;
    setPreferredSize(new Dimension(x, y));

    repaint();
  }

  @Override
  protected void paintComponent(Graphics g) {
    g.setColor(getBackground());
    g.fillRect(0, 0, getWidth(), getHeight());

    if (diskImage == null) {
      return;
    }

    var bam = diskImage.getBAM();

    var fontMetrics = g.getFontMetrics();
    var descent = fontMetrics.getDescent();

    // Track numbers.
    g.setColor(getForeground());
    var x = leftSpace + left + border + box / 2; // Center of first horizontal box.
    var y = topSpace / 2 + height - descent; // Base line of top space.
    for (var track = 1; track < diskImage.getTracks(); track += 10) {
      var description = Integer.toString(track);
      // Draw centered regarding to box.
      var bounds = fontMetrics.getStringBounds(description, g);
      g.drawString(description, (int) Math.round(x - bounds.getWidth() / 2), y);
      x += 10 * (box + border);
    }

    // Sector numbers.
    g.setColor(getForeground());
    y = topSpace + height + border + box / 2; // Center of first vertical box.
    for (var sector = 0; sector < diskImage.getSectors(); sector += 10) {
      var description = Integer.toString(sector);
      // Draw centered regarding to box.
      var bounds = fontMetrics.getStringBounds(description, g);
      g.drawString(description, (int) Math.ceil(left - bounds.getWidth()) + (left - leftSpace) / 2, (int) Math.round(y + bounds.getHeight() / 2) - descent);
      y += 10 * (box + border);
    }

    // BAM map.
    x = leftSpace + left;
    for (var track = 1; track <= diskImage.getTracks(); track++) {
      y = topSpace + height;
      var spt = diskImage.getSectors(track);
      g.setColor(Color.BLACK);
      g.fillRect(x, y, border + box + border, border + spt * (box + border));
      for (var sector = 0; sector < spt; sector++) {
        var color = used;
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

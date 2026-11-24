package de.heiden.c64dt.gui.swing;

import de.heiden.c64dt.gui.C64Color;

import java.awt.*;

/**
 * C64 colors in Swing representation.
 */
class SwingColors {
  /**
   * C64 colors in Swing representation.
   */
  public static final Color[] COLOR = new Color[C64Color.values().length];

  static {
    var colors = C64Color.values();
    for (var i = 0; i < colors.length; i++) {
      var color = colors[i];
      COLOR[i] = new Color(color.getRed(), color.getGreen(), color.getBlue());
    }
  }

}

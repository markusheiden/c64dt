package de.heiden.c64dt.gui;

import java.awt.*;

/**
 * C64 colors in swing representation.
 */
class SwingColors {
  /**
   * C64 colors in swing representation.
   */
  public static final Color[] COLOR = new Color[C64Color.values().length];

  static {
    C64Color[] colors = C64Color.values();
    for (int i = 0; i < colors.length; i++) {
      C64Color color = colors[i];
      COLOR[i] = new Color(color.getRed(), color.getGreen(), color.getBlue());
    }
  }

}

package de.heiden.c64dt.gui.javafx;

import de.heiden.c64dt.gui.C64Color;
import javafx.scene.paint.Color;

/**
 * C64 colors in swing representation.
 */
class JavaFXColors {
  /**
   * C64 colors in swing representation.
   */
  public static final Color[] COLOR = new Color[C64Color.values().length];

  static {
    C64Color[] colors = C64Color.values();
    for (int i = 0; i < colors.length; i++) {
      C64Color color = colors[i];
      COLOR[i] = Color.rgb(color.getRed(), color.getGreen(), color.getBlue());
    }
  }
}

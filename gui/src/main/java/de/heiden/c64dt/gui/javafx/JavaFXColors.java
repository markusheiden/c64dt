package de.heiden.c64dt.gui.javafx;

import de.heiden.c64dt.gui.C64Color;
import javafx.scene.paint.Color;

/**
 * C64 colors in JavaFX representation.
 */
class JavaFXColors {
  /**
   * C64 colors in JavaFX representation.
   */
  public static final Color[] COLOR = new Color[C64Color.values().length];

  static {
    var colors = C64Color.values();
    for (var i = 0; i < colors.length; i++) {
      var color = colors[i];
      COLOR[i] = Color.rgb(color.getRed(), color.getGreen(), color.getBlue());
    }
  }
}

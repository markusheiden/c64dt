package de.heiden.c64dt.javafx;

import javafx.scene.paint.Color;

import java.awt.image.IndexColorModel;

/**
 * Indexed color model with C64 colors.
 */
public class C64IndexColorModel extends IndexColorModel {
  private static final byte[] R;
  private static final byte[] G;
  private static final byte[] B;

  static {
    // create color model components
    C64Color[] colors = C64Color.values();
    R = new byte[colors.length];
    G = new byte[colors.length];
    B = new byte[colors.length];
    for (int i = 0; i < colors.length; i++) {
      Color color = colors[i].getColor();
      R[i] = (byte) color.getRed();
      G[i] = (byte) color.getGreen();
      B[i] = (byte) color.getBlue();
    }
  }

  /**
   * Constructor.
   */
  public C64IndexColorModel() {
    super(8, 16, R, G, B);
  }
}
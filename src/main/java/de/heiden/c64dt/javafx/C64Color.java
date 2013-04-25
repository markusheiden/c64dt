package de.heiden.c64dt.javafx;

import javafx.scene.paint.Color;

/**
 * Color constants in correct order.
 * <p/>
 * From: Philip "Pepto" Timmermann, pepto@pepto.de
 */
public enum C64Color {
  BLACK(0x00, 0x00, 0x00),
  WHITE(0xFF, 0xFF, 0xFF),
  RED(0x68, 0x37, 0x2B),
  CYAN(0x70, 0xA4, 0xB2),
  PURPLE(0x6F, 0x3D, 0x86),
  GREEN(0x58, 0x8D, 0x43),
  BLUE(0x35, 0x28, 0x79),
  YELLOW(0xB8, 0xC7, 0x6F),
  ORANGE(0x6F, 0x4F, 0x25),
  BROWN(0x43, 0x39, 0x00),
  LIGHT_RED(0x9A, 0x67, 0x59),
  DARK_GRAY(0x44, 0x44, 0x44),
  GRAY(0x6C, 0x6C, 0x6C),
  LIGHT_GREEN(0x9A, 0xD2, 0x84),
  LIGHT_BLUE(0x6C, 0x5E, 0xB5),
  LIGHT_GRAY(0x95, 0x95, 0x95);

  /**
   * Red as byte.
   */
  private final int r;

  /**
   * Green as byte.
   */
  private final int g;

  /**
   * Blue as byte.
   */
  private final int b;

  /**
   * The color.
   */
  private final Color color;

  /**
   * Constructor.
   *
   * @param r red
   * @param g green
   * @param b blue
   */
  private C64Color(int r, int g, int b) {
    this.r = r;
    this.g = g;
    this.b = b;
    this.color = Color.rgb(r, g, b);
  }

  /**
   * Integer representation of color.
   */
  public int toArgb() {
    return (0xFF << 24) + (r << 16) + (g << 8) + b;
  }

  /**
   * Color.
   */
  public Color getColor() {
    return color;
  }

  /**
   * All colors in their integer representation.
   */
  public static int[] colorsAsArgb() {
    C64Color[] colors = values();
    int[] result = new int[colors.length];
    for (int i = 0; i < result.length; i++) {
      result[i] = colors[i].toArgb();
    }

    return result;
  }
}

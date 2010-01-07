package de.heiden.c64dt.gui;

import javax.swing.JComponent;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

/**
 * Base class for component displaying c64 content.
 */
public abstract class JC64ScreenComponent extends JComponent
{
  /**
   * Constructor.
   *
   * @param width width in pixel
   * @param height height in pixel
   * @param factor zoom factor
   */
  protected JC64ScreenComponent(int width, int height, int factor)
  {
    _height = height;
    _width = width;
    _factor = factor;
    _colors = new Object[C64Color.values().length];

    Dimension size = new Dimension(width * factor, height * factor);
    setPreferredSize(size);
    setSize(size);
  }

  /**
   * Backing image.
   */
  public BufferedImage getImage()
  {
    return _image;
  }

  /**
   * Native color representations of the C64 colors.
   * For optimal drawing speed.
   */
  public Object[] getColors()
  {
    return _colors;
  }

  @Override
  public final void paintComponent(Graphics g)
  {
    createScreenImage();
    doPaintComponent(g);
    drawImage(g);
  }

  /**
   * Do custom updating.
   *
   * @param g graphics
   */
  protected abstract void doPaintComponent(Graphics g);

  /**
   * Draw backing image with the fixed defined size.
   *
   * @param g graphics
   */
  protected void drawImage(Graphics g)
  {
    drawImageFixed(g);
  }

  /**
   * Draw backing image with the fixed defined size.
   *
   * @param g graphics
   */
  protected void drawImageFixed(Graphics g)
  {
    g.drawImage(_image, 0, 0, _width * _factor, _height * _factor, null);
  }

  /**
   * Draw backing image fitting into the current component size.
   *
   * @param g graphics
   */
  protected void drawImageResized(Graphics g)
  {
    g.drawImage(_image, 0, 0, getWidth(), getHeight(), null);
  }

  /**
   * Lazily create image for screen display.
   */
  protected void createScreenImage()
  {
    if (_image != null)
    {
      return;
    }

    GraphicsConfiguration gc = getGraphicsConfiguration();

    // create buffered image
    _image = gc.createCompatibleImage(_width, _height);
    _image.setAccelerationPriority(1);

    // convert colors to device specific colors
    ColorModel cm = gc.getColorModel();
    C64Color[] colors = C64Color.values();
    for (int i = 0; i < colors.length; i++)
    {
      _colors[i] = cm.getDataElements(colors[i].getColor().getRGB(), null);
    }
  }

  //
  // attributes
  //

  private final int _width;
  private final int _height;
  private final int _factor;

  private BufferedImage _image;
  private final Object[] _colors;
}

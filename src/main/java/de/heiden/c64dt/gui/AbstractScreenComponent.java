package de.heiden.c64dt.gui;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

/**
 * Base class for component displaying c64 content.
 */
public abstract class AbstractScreenComponent extends Canvas
{
  /**
   * Constructor.
   *
   * @param width width in pixel
   * @param height height in pixel
   * @param factor zoom factor
   */
  protected AbstractScreenComponent(int width, int height, int factor)
  {
    _height = height;
    _width = width;
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
  public final void paint(Graphics g)
  {
    createScreenImage();
    drawImage(g);
  }

  @Override
  public final void update(Graphics g)
  {
    createScreenImage();
    doUpdate(g);
    drawImage(g);
  }

  /**
   * Do custom updating.
   *
   * @param g graphics
   */
  protected abstract void doUpdate(Graphics g);

  /**
   * Draw backing image.
   *
   * @param g graphics
   */
  protected void drawImage(Graphics g)
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

  private BufferedImage _image;
  private final Object[] _colors;
}

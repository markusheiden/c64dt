package de.heiden.c64dt.gui;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MemoryImageSource;

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
  protected JC64ScreenComponent(int width, int height, double factor)
  {
    _height = height;
    _width = width;
    _factor = factor;

    // create color model
    C64Color[] colors = C64Color.values();
    byte[] r = new byte[colors.length];
    byte[] g = new byte[colors.length];
    byte[] b = new byte[colors.length];
    for (int i = 0; i < colors.length; i++)
    {
      Color color = colors[i].getColor();
      r[i] = (byte) color.getRed();
      g[i] = (byte) color.getGreen();
      b[i] = (byte) color.getBlue();
    }
    ColorModel colorModel = new IndexColorModel(8, 16, r, g, b);

    // create image data
    _imageData = new byte[_width * _height];

    // create image source
    _imageSource = new MemoryImageSource(_width, _height, colorModel, _imageData, 0, _width);
    _imageSource.setAnimated(true);
    _imageSource.setFullBufferUpdates(true);

    Dimension size = new Dimension((int) Math.round(width * factor), (int) Math.round(height * factor));
    setPreferredSize(size);
    setSize(size);
  }

  /**
   * Width of backing image.
   */
  public int getImageWidth()
  {
    return _width;
  }

  /**
   * Height of backing image.
   */
  public int getImageHeight()
  {
    return _height;
  }

  /**
   * Backing image.
   */
  public byte[] getImageData()
  {
    return _imageData;
  }

  @Override
  public final void paintComponent(Graphics g)
  {
    createImage(g);
    doPaintComponent(g);
    _imageSource.newPixels();
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
    g.drawImage(_image, 0, 0, (int) Math.round(_width * _factor), (int) Math.round(_height * _factor), null);
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
  private void createImage(Graphics graphics)
  {
    if (_image != null)
    {
      return;
    }

    // create image
    _image = createImage(_imageSource);
    _image.setAccelerationPriority(1);
  }

  //
  // attributes
  //

  private final int _width;
  private final int _height;
  private final double _factor;

  private final byte[] _imageData;
  private final MemoryImageSource _imageSource;
  private Image _image;
}

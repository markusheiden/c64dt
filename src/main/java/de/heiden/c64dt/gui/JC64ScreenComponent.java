package de.heiden.c64dt.gui;

import javax.swing.JComponent;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
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
    _colors = new int[C64Color.values().length];

    // create image source
    _imageData = new int[_width * _height];
    _imageSource = new MemoryImageSource(_width, _height, _imageData, 0, _width);
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
  public int[] getImageData()
  {
    return _imageData;
  }

  /**
   * Color representations of the C64 colors.
   */
  public int[] getColors()
  {
    return _colors;
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
  private void createImage(Graphics g)
  {
    if (_image != null)
    {
      return;
    }

    GraphicsConfiguration gc = getGraphicsConfiguration();

    // create image
    _image = createImage(_imageSource);
    _image.setAccelerationPriority(1);

    // convert colors to device specific colors
    C64Color[] colors = C64Color.values();
    for (int i = 0; i < colors.length; i++)
    {
      _colors[i] = colors[i].getColor().getRGB();
    }
  }

  //
  // attributes
  //

  private final int _width;
  private final int _height;
  private final double _factor;

  private final int[] _colors;
  private final int[] _imageData;
  private final MemoryImageSource _imageSource;
  private Image _image;
}

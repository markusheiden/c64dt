package de.heiden.c64dt.gui;

import java.awt.Color;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.MemoryImageSource;

/**
 * Base class for component displaying c64 rgb color content.
 */
public abstract class JC64CommonComponent extends JC64Component
{
  /**
   * Constructor.
   *
   * @param width width in pixel
   * @param height height in pixel
   * @param factor zoom factor
   */
  protected JC64CommonComponent(int width, int height, double factor)
  {
    super(width, height, factor);

    // create color model
    ColorModel colorModel = ColorModel.getRGBdefault();

    // create image data
    _imageData = new int[getImageWidth() * getImageHeight()];

    // create image source
    _imageSource = new MemoryImageSource(width, height, colorModel, _imageData, 0, width);
    _imageSource.setAnimated(true);
    _imageSource.setFullBufferUpdates(true);
  }

  /**
   * Backing image.
   */
  public int[] getImageData()
  {
    return _imageData;
  }

  //
  // attributes
  //

  private final int[] _imageData;
}

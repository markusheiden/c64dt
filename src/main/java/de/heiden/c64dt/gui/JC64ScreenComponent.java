package de.heiden.c64dt.gui;

import java.awt.Color;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;

/**
 * Base class for component displaying c64 indexed color content.
 */
public abstract class JC64ScreenComponent extends JC64Component
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
    super(width, height, factor);

    _colorModel = new C64IndexColorModel();

    // create image data
    _imageData = new byte[getImageWidth() * getImageHeight()];

    // create image source
    _imageSource = new MemoryImageSource(width, height, _colorModel, _imageData, 0, width);
    _imageSource.setAnimated(true);
    _imageSource.setFullBufferUpdates(true);
  }

  /**
   * Set foreground color.
   *
   * @param foreground foreground color
   */
  @Override
  public void setForeground(C64Color foreground)
  {
    _foreground = (byte) foreground.ordinal();
    super.setForeground(foreground.getColor());
  }

  @Override
  public void setForeground(Color fg)
  {
    throw new UnsupportedOperationException("RGB colors not supported. Use C64 colors instead.");
  }

  /**
   * Set background color.
   *
   * @param background background color
   */
  public void setBackground(C64Color background)
  {
    _background = background.ordinal();
    super.setBackground(background.getColor());
  }

  @Override
  public void setBackground(Color bg)
  {
    throw new UnsupportedOperationException("RGB colors not supported. Use C64 colors instead.");
  }

  /**
   * Backing image.
   */
  public byte[] getImageData()
  {
    return _imageData;
  }

  /**
   * Update backing image data at image source.
   *
   * @param imageData new backing image data
   */
  protected void updateImageData(byte[] imageData)
  {
    assert imageData != null : "Precondition: imageData != null";
    assert imageData.length == getImageData().length : "Precondition: imageData.length == getImageData().length";

    _imageData = imageData;
    _imageSource.newPixels(imageData, _colorModel, 0, getImageWidth());
  }

  //
  // attributes
  //

  protected int _foreground;
  protected int _background;

  private final ColorModel _colorModel;
  private byte[] _imageData;
}

package de.heiden.c64dt.gui;

import java.awt.*;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;

/**
 * Base class for component displaying c64 indexed color content.
 */
public abstract class JC64ScreenComponent extends JC64Component {
  //
  // attributes
  //

  protected int _foreground;
  protected int _background;

  private final int _offset;
  private final int _lineLength;
  private final ColorModel _colorModel;
  private byte[] _imageData;

  //
  //
  //

  /**
   * Constructor.
   *
   * @param width width in pixel
   * @param height height in pixel
   * @param factor zoom factor
   */
  protected JC64ScreenComponent(int offset, int width, int lineLength, int height, double factor) {
    super(width, height, factor, false);

    _offset = offset;
    _lineLength = lineLength;

    _colorModel = new C64IndexColorModel();

    // create image data
    _imageData = new byte[offset + lineLength * height];

    // create image source
    _imageSource = new MemoryImageSource(width, height, _colorModel, _imageData, offset, lineLength);
    _imageSource.setAnimated(true);
    _imageSource.setFullBufferUpdates(true);
  }

  @Override
  public void setForeground(int foreground) {
    _foreground = foreground;
    super.setForeground(SwingColors.COLOR[foreground]);
  }

  @Override
  public void setForeground(Color fg) {
    throw new UnsupportedOperationException("RGB colors not supported. Use C64 colors instead.");
  }

  @Override
  public void setBackground(int background) {
    _background = background;
    super.setBackground(SwingColors.COLOR[background]);
  }

  @Override
  public void setBackground(Color bg) {
    throw new UnsupportedOperationException("RGB colors not supported. Use C64 colors instead.");
  }

  /**
   * Backing image.
   */
  public byte[] getImageData() {
    return _imageData;
  }

  /**
   * Update backing image data at image source.
   *
   * @param imageData new backing image data
   */
  protected void updateImageData(byte[] imageData) {
    assert imageData != null : "Precondition: imageData != null";
    assert imageData.length == getImageData().length : "Precondition: imageData.length == getImageData().length";

    _imageData = imageData;
    _imageSource.newPixels(imageData, _colorModel, _offset, _lineLength);
  }
}

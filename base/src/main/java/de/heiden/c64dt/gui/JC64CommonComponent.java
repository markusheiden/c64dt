package de.heiden.c64dt.gui;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;

/**
 * Base class for component displaying default rgb color c64 content.
 */
public abstract class JC64CommonComponent extends JC64Component {
  /**
   * Color model.
   */
  private final ColorModel _colorModel;

  /**
   * Image.
   */
  private int[] _imageData;

  /**
   * Constructor.
   *
   * @param width width in pixel
   * @param height height in pixel
   * @param factor zoom factor
   * @param resizable Is the backing image resizable?
   */
  protected JC64CommonComponent(int width, int height, double factor, boolean resizable) {
    super(width, height, factor, resizable);

    // create color model
    _colorModel = ColorModel.getRGBdefault();

    onResize();
    if (resizable) {
      addComponentListener(new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
          onResize();
        }
      });
    }
  }

  /**
   * Resizing.
   */
  private void onResize() {
    // create image data
    _imageData = new int[getImageWidth() * getImageHeight()];

    // create image source
    _imageSource = new MemoryImageSource(getImageWidth(), getImageHeight(), _colorModel, _imageData, 0, getImageWidth());
    _imageSource.setAnimated(true);
    _imageSource.setFullBufferUpdates(true);
  }

  /**
   * Backing image.
   */
  public int[] getImageData() {
    return _imageData;
  }
}

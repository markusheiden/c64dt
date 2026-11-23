package de.heiden.c64dt.gui.swing;

import de.heiden.c64dt.gui.C64Color;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.MemoryImageSource;

/**
 * Base class for component displaying C64 content.
 */
public abstract class JC64Component extends JComponent {
  /**
   * Width.
   */
  private int _width;

  /**
   * Height.
   */
  private int _height;

  /**
   * Scale factor for image.
   */
  private final double _factor;

  /**
   * Image source.
   */
  protected MemoryImageSource _imageSource;

  /**
   * Image.
   */
  private Image _image;

  /**
   * Constructor.
   *
   * @param width Width in pixel.
   * @param height Height in pixel.
   * @param factor Zoom factor.
   * @param resizable Is the backing image resizable?
   */
  protected JC64Component(int width, int height, double factor, boolean resizable) {
    _height = height;
    _width = width;
    _factor = factor;

    Dimension size = new Dimension((int) Math.ceil(width * factor), (int) Math.ceil(height * factor));
    setPreferredSize(size);
    setSize(size);
    setMinimumSize(new Dimension(0, 0));

    if (resizable) {
      addComponentListener(new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
          // Compute image size from new component size.
          _width = Math.max(0, (int) Math.round(e.getComponent().getWidth() / _factor));
          _height = Math.max(0, (int) Math.round(e.getComponent().getHeight() / _factor));

          // The image needs to be rebuilt, so reset it now. The sub class has to rebuild it.
          _imageSource = null;
          _image = null;
        }
      });
    }
  }

  /**
   * Width of backing image.
   */
  public int getImageWidth() {
    return _width;
  }

  /**
   * Height of backing image.
   */
  public int getImageHeight() {
    return _height;
  }

  /**
   * Set foreground color.
   *
   * @param foreground Foreground color index.
   */
  public void setForeground(int foreground) {
    setForeground(SwingColors.COLOR[foreground]);
  }

  /**
   * Set foreground color.
   *
   * @param foreground Foreground color.
   */
  public void setForeground(C64Color foreground) {
    setForeground(foreground.ordinal());
  }

  /**
   * Set background color.
   *
   * @param background Background color index.
   */
  public void setBackground(int background) {
    setBackground(SwingColors.COLOR[background]);
  }

  /**
   * Set background color.
   *
   * @param background Background color.
   */
  public void setBackground(C64Color background) {
    setBackground(background.ordinal());
  }

  @Override
  public final void paintComponent(Graphics g) {
    if (_image == null) {
      createImage();
    }
    doPaintComponent(g);
    drawImage(g);
  }

  /**
   * Notify image source that the backing image data has been changed.
   */
  protected void updateImageData() {
    _imageSource.newPixels();
  }

  /**
   * Do custom updating.
   *
   * @param g Graphics.
   */
  protected abstract void doPaintComponent(Graphics g);

  /**
   * Draw backing image with the fixed defined size.
   *
   * @param g Graphics.
   */
  protected void drawImage(Graphics g) {
    drawImageFixed(g);
  }

  /**
   * Draw backing image with the fixed defined size.
   *
   * @param g Graphics.
   */
  protected void drawImageFixed(Graphics g) {
    g.drawImage(_image, 0, 0, (int) Math.round(_width * _factor), (int) Math.round(_height * _factor), null);
  }

  /**
   * Draw backing image fitting into the current component size.
   *
   * @param g Graphics.
   */
  protected void drawImageResized(Graphics g) {
    g.drawImage(_image, 0, 0, getWidth(), getHeight(), null);
  }

  /**
   * Lazily create image for screen display.
   */
  private void createImage() {
    // Create image.
    _image = createImage(_imageSource);
    _image.setAccelerationPriority(1);
  }
}

package de.heiden.c64dt.javafx;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * Base class for component displaying c64 content.
 */
public abstract class JC64Component extends Pane {
  /**
   * Width,
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
   * Should the component be resizable?.
   */
  private final boolean _resizable;

  /**
   * The canvas.
   */
  private final Canvas _canvas;

  /**
   * Image.
   */
  protected WritableImage _image;

  /**
   * Foreground color.
   */
  private Color _foreground;

  /**
   * Background color.
   */
  private Color _background;

  /**
   * Constructor.
   *
   * @param width width in pixel
   * @param height height in pixel
   * @param factor zoom factor
   * @param resizable Is the backing image resizable?
   */
  protected JC64Component(int width, int height, double factor, boolean resizable) {
    _height = height;
    _width = width;
    _factor = factor;
    _resizable = resizable;

    _canvas = new Canvas(Math.ceil(width * factor), Math.ceil(height * factor));
    getChildren().add(_canvas);

    _image = new WritableImage((int) _canvas.getWidth(), (int) _canvas.getHeight());
  }

  @Override
  protected void layoutChildren() {
    if (_resizable) {
      // compute image size from new component size
      _width = Math.max(0, (int) Math.floor(getWidth() / _factor));
      _height = Math.max(0, (int) Math.floor(getHeight() / _factor));

      _canvas.setWidth(_width);
      _canvas.setHeight(_height);

      _image = new WritableImage((int) _canvas.getWidth(), (int) _canvas.getHeight());
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
   * @param foreground foreground color index
   */
  public void setForegroundColor(int foreground) {
    setForegroundColor(C64Color.values()[foreground]);
  }

  /**
   * Set foreground color.
   *
   * @param foreground foreground color
   */
  public void setForegroundColor(C64Color foreground) {
    _foreground = foreground.getColor();
  }

  /**
   * Set background color.
   *
   * @param background background color index
   */
  public void setBackgroundColor(int background) {
    setBackgroundColor(C64Color.values()[background]);
  }

  /**
   * Set background color.
   *
   * @param background background color
   */
  public void setBackgroundColor(C64Color background) {
    _background = background.getColor();
  }
}

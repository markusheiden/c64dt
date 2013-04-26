package de.heiden.c64dt.javafx;

import de.heiden.c64dt.gui.C64Color;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

/**
 * Base class for component displaying c64 content.
 */
public abstract class C64Component extends Region {
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
  protected final boolean _resizable;

  /**
   * The canvas.
   */
  private final ImageView _view;

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
  protected C64Component(int width, int height, double factor, boolean resizable) {
    _height = height;
    _width = width;
    _factor = factor;
    _resizable = resizable;

    setWidth(Math.ceil(width * factor));
    setHeight(Math.ceil(height * factor));

    _view = new ImageView(new WritableImage(_width, _height));
    _view.setTranslateX(_width * (_factor - 1) / 2);
    _view.setTranslateY(_height * (_factor - 1) / 2);
    _view.setScaleX(_factor);
    _view.setScaleY(_factor);
    getChildren().add(_view);
  }

  /**
   * Init fields.
   */
  private void onResize() {
    // compute image size from new component size
    _width = Math.max(0, (int) (Math.floor(getWidth() / _factor)));
    _height = Math.max(0, (int) (Math.floor(getHeight() / _factor)));

    _view.imageProperty().set(new WritableImage(_width, _height));
    restoreImage();
  }

  /**
   * Restore content of backing image.
   */
  protected void restoreImage() {
  }

  /**
   * Writer for backing image.
   */
  protected final PixelWriter getPixelWriter() {
    return ((WritableImage) _view.getImage()).getPixelWriter();
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
   * Foreground color.
   */
  public Color getForegroundColor() {
    return _foreground;
  }

  /**
   * Set foreground color.
   *
   * @param foreground foreground color index
   */
  public void setForegroundColor(int foreground) {
    _foreground = JavaFXColors.COLOR[foreground];
  }

  /**
   * Set foreground color.
   *
   * @param foreground foreground color
   */
  public void setForegroundColor(C64Color foreground) {
    setForegroundColor(foreground.ordinal());
  }

  /**
   * Background color.
   */
  public Color getBackgroundColor() {
    return _background;
  }

  /**
   * Set background color.
   *
   * @param background background color index
   */
  public void setBackgroundColor(int background) {
    _background = JavaFXColors.COLOR[background];
  }

  /**
   * Set background color.
   *
   * @param background background color
   */
  public void setBackgroundColor(C64Color background) {
    setBackgroundColor(background.ordinal());
  }
}

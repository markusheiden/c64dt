package de.heiden.c64dt.javafx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.PixelFormat;
import javafx.stage.Stage;

import java.nio.IntBuffer;

/**
 * Base class for component displaying c64 indexed color content.
 */
public abstract class JC64ScreenComponent extends JC64Component {
  //
  // attributes
  //

  /**
   * Starting offset in image array.
   */
  private final int _offset;

  /**
   * Number of bytes per line.
   */
  private final int _lineLength;

  /**
   * Pixel format.
   */
  private final PixelFormat<IntBuffer> format = PixelFormat.getIntArgbPreInstance();

  /**
   * Pixels converted to rgb representation.
   */
  private final int[] pixels;

  //
  //
  //

  /**
   * Constructor.
   *
   * @param offset Offset of first pixel in byte buffer
   * @param width width in pixel
   * @param lineLength Length of a line in the byte buffer
   * @param height height in pixel
   * @param factor zoom factor
   */
  protected JC64ScreenComponent(int offset, int width, int lineLength, int height, double factor) {
    super(width, height, factor, false);

    _offset = offset;
    _lineLength = lineLength;

    pixels = new int[width * height];
  }

  /**
   * Update backing image.
   *
   * @param imageData new backing image data
   */
  protected void updateImageData(byte[] imageData) {
    assert imageData != null : "Precondition: imageData != null";

    int[] colors = C64Color.colorsAsArgb();
    for (int p = 0, i = _offset, row = 0; row < getImageHeight(); row++) {
      for (int column = 0; column < getImageWidth(); column++) {
        pixels[p++] = colors[imageData[i++]];
      }
      i += _lineLength - getImageWidth();
    }

    getPixelWriter().setPixels(0, 0, getImageWidth(), getImageHeight(), format, pixels, 0, getImageWidth());
  }

  //
  // test
  //

  /**
   * Start a small demo app for this control.
   */
  public static void main(String[] args) {
    TestApp.launch(TestApp.class);
  }

  /**
   * Demo app for this control.
   */
  public static class TestApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
      JC64ScreenComponent screen = new JC64ScreenComponent(0, 160, 160, 160, 1) {
      };
      byte[] data = new byte[screen.getImageWidth() * screen.getImageHeight()];
      for (int i = 0; i < data.length; i++) {
        data[i] = (byte) ((i / 1600) & 0x0F);
      }
      screen.updateImageData(data);

      stage.setScene(new Scene(screen, screen.getWidth(), screen.getHeight()));
      stage.show();
    }
  }
}

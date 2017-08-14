package de.heiden.c64dt.gui.javafx;

import de.heiden.c64dt.gui.C64Color;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.stage.Stage;

import java.nio.IntBuffer;

import static org.bitbucket.cowwoc.requirements.core.Requirements.requireThat;

/**
 * Base class for component displaying c64 indexed color content.
 */
public abstract class C64ScreenComponent extends C64Component {
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
   * Pixels converted to ARGB representation.
   */
  private final int[] pixels;

  /**
   * C64 colors in ARGB representation.
   */
  private static final int[] COLOR = new int[C64Color.values().length];

  static {
    C64Color[] colors = C64Color.values();
    for (int i = 0; i < colors.length; i++) {
      C64Color color = colors[i];
      COLOR[i] = (0xFF << 24) | (color.getRed() << 16) | (color.getGreen() << 8) | color.getBlue();
    }
  }

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
  protected C64ScreenComponent(int offset, int width, int lineLength, int height, double factor) {
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
    requireThat(imageData, "imageData").isNotNull();

    final int width = getImageWidth();
    final int height = getImageHeight();
    final int lineOffset = _lineLength - width;

    // convert color indexes to ARGB colors
    for (int p = 0, d = _offset, row = 0; row < height; row++) {
      for (int column = 0; column < width; column++) {
        pixels[p++] = COLOR[imageData[d++]];
      }
      d += lineOffset;
    }

    final PixelWriter writer = getPixelWriter();
    Platform.runLater(() -> writer.setPixels(0, 0, width, height, format, pixels, 0, width));
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
      C64ScreenComponent screen = new C64ScreenComponent(0, 160, 160, 160, 1) {
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

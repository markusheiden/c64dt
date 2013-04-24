package de.heiden.c64dt.javafx;

import javafx.scene.image.PixelFormat;

import java.nio.ByteBuffer;

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
  private final PixelFormat<ByteBuffer> format = PixelFormat.createByteIndexedInstance(
    new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15});

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
  }

  /**
   * Update backing image.
   *
   * @param imageData new backing image data
   */
  protected void updateImageData(byte[] imageData) {
    assert imageData != null : "Precondition: imageData != null";

    getPixelWriter().setPixels(0, 0, getImageWidth(), getImageHeight(), format, imageData, _offset, _lineLength);
  }
}

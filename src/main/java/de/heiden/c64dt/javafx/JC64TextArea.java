package de.heiden.c64dt.javafx;

import de.heiden.c64dt.charset.C64Charset;
import de.heiden.c64dt.util.ResourceLoader;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.StringTokenizer;

/**
 * Component for displaying C64 text.
 */
public class JC64TextArea extends JC64Component {
  /**
   * Row height in pixel.
   */
  public static final int ROW_HEIGHT = 8;

  /**
   * Columns width in pixel.
   */
  public static final int COLUMN_WIDTH = 8;

  private int _columns;
  private int _rows;
  private final double _factor;
  private byte[][] _chars;
  private Color[][] _foregrounds;
  private Color[][] _backgrounds;

  private boolean _upper;
  private C64Charset _charset;
  private final int[] _charsetROM;

  private static int[] _defaultCharsetROM;

  //
  //
  //

  /**
   * Constructor.
   * The lower charset is used as default.
   *
   * @param columns character columns to display
   * @param rows character rows to display
   * @param factor zoom factor
   * @param resizable Is the backing image resizable?
   */
  public JC64TextArea(int columns, int rows, double factor, boolean resizable) {
    super(columns * COLUMN_WIDTH, rows * ROW_HEIGHT, factor, resizable);

    _columns = columns;
    _rows = rows;
    _factor = factor;

    setCharset(false);
    _charsetROM = getDefaultCharset();

    onResize();
    clear();
  }

  /**
   * Init fields.
   */
  private void onResize() {
    _columns = (int) Math.ceil(getImageWidth() / COLUMN_WIDTH);
    _rows = (int) Math.ceil(getImageHeight() / ROW_HEIGHT);

    _chars = new byte[_rows][_columns];
    _foregrounds = new Color[_rows][_columns];
    _backgrounds = new Color[_rows][_columns];

    restoreImage();
  }

  /**
   * Default charset.
   */
  private static int[] getDefaultCharset() {
    if (_defaultCharsetROM == null) {
      try {
        _defaultCharsetROM = ResourceLoader.load(0x1000, "/roms/character/display.bin");
      } catch (Exception e) {
        throw new IllegalArgumentException("Unable to load default charset", e);
      }
    }

    return _defaultCharsetROM;
  }

  /**
   * Number of character rows.
   */
  public int getRows() {
    return _rows;
  }

  /**
   * Height of rows.
   */
  public int getRowHeight() {
    return (int) Math.round(ROW_HEIGHT * _factor);
  }

  /**
   * Number of character columns.
   */
  public int getColumns() {
    return _columns;
  }

  /**
   * Height of rows.
   */
  public int getColumnWidth() {
    return (int) Math.round(COLUMN_WIDTH * _factor);
  }

  /**
   * Set charset to use.
   *
   * @param upper use upper case only charset?
   */
  public final void setCharset(boolean upper) {
    _upper = upper;
    _charset = C64Charset.charset(upper);
  }

  /**
   * Draw a string to the screen.
   *
   * @param column column
   * @param row row
   * @param s characters
   */
  public void setText(int column, int row, String s) {
    if (!s.contains("\n")) {
      // optimization for single line text
      setText(column, row, _charset.toBytes(s));
      return;
    }

    // handle multi line text
    StringTokenizer tokenizer = new StringTokenizer(s, "\n");
    for (int r = row; tokenizer.hasMoreTokens() && r < _rows; r++) {
      setText(column, r, tokenizer.nextToken());
    }
  }

  /**
   * Draw a string with inverted characters to the screen.
   *
   * @param column column
   * @param row row
   * @param s characters
   */
  public void setTextInverted(int column, int row, String s) {
    byte[] characters = _charset.toBytes(s);
    for (int i = 0; i < characters.length; i++) {
      characters[i] |= 0x80;
    }
    setText(column, row, characters);
  }

  /**
   * Draw a string to the screen.
   *
   * @param column column
   * @param row row
   * @param s characters in C64 encoding
   */
  public void setText(int column, int row, byte... s) {
    PixelWriter writer = getPixelWriter();
    for (int i = 0, c = column; i < s.length && c < _columns; i++, c++) {
      setTextInternal(c, row, s[i], writer);
    }
  }

  /**
   * Draw a single character to the screen.
   *
   * @param column column
   * @param row row
   * @param c character in C64 encoding
   */
  public void setText(int column, int row, byte c) {
    setTextInternal(column, row, c, getPixelWriter());
  }

  /**
   * Draw a single character to the screen.
   *
   * @param column column
   * @param row row
   * @param c character in C64 encoding
   * @param writer Pixel writer for backing image
   */
  protected void setTextInternal(int column, int row, byte c, PixelWriter writer) {
    if (column >= _columns || row >= _rows) {
      // early exit, when the text exceeds the buffer.
      // this may happen, e.g. when the component has been resized to a smaller size than the initial size.
      return;
    }

    _chars[row][column] = c;
    _foregrounds[row][column] = getForegroundColor();
    _backgrounds[row][column] = getBackgroundColor();

    paintCharacter(column, row, writer);
  }

  /**
   * Clear component with background color.
   */
  public void clear() {
    byte space = _charset.toBytes(" ")[0];
    Color foreground = getForegroundColor();
    Color background = getBackgroundColor();
    for (int row = 0; row < _rows; row++) {
      byte[] charRow = _chars[row];
      Color[] foregroundRow = _foregrounds[row];
      Color[] backgroundRow = _backgrounds[row];
      for (int column = 0; column < _columns; column++) {
        charRow[column] = space;
        foregroundRow[column] = foreground;
        backgroundRow[column] = background;
      }
    }

    restoreImage();
  }

  @Override
  protected void restoreImage() {
    PixelWriter writer = getPixelWriter();
    for (int row = 0; row < _rows; row++) {
      for (int column = 0; column < _columns; column++) {
        paintCharacter(column, row, writer);
      }
    }
  }

  /**
   * Paint a single character onto the screen.
   *
   * @param column column
   * @param row row
   * @param writer Pixel writer for backing image
   */
  private void paintCharacter(int column, int row, PixelWriter writer) {
    Color foregroundColor = _foregrounds[row][column];
    if (foregroundColor == null) {
      return;
    }
    Color backgroundColor = _backgrounds[row][column];
    if (backgroundColor == null) {
      return;
    }

    int charOffset = _upper ? 0x0000 : 0x0800;
    int charPtr = charOffset + (_chars[row][column] & 0xFF) * 8;
    for (int y = row * 8, lastY = y + 8; y < lastY; y++) {
      int data = _charsetROM[charPtr++];
      for (int x = column * 8, bit = 0x80; bit > 0; x++, bit = bit >> 1) {
        writer.setColor(x, y, (data & bit) != 0 ? foregroundColor : backgroundColor);
      }
    }
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
      JC64TextArea text = new JC64TextArea(40, 25, 2, true);

      text.setForegroundColor(C64Color.LIGHT_BLUE);
      text.setBackgroundColor(C64Color.BLUE);
      text.clear();

      text.setCharset(false);
      text.setText(0, 0, "This is a test azAZ");
      text.setTextInverted(0, 1, "This is a test azAZ");
      text.setText(0, 2, "@[] !\"#$%&'()*+,-./1234567890:;<=>?|_");

      text.setCharset(true);
      text.setText(0, 3, "THIS IS A TEST AZ");
      text.setTextInverted(0, 4, "THIS IS A TEST AZ");
      text.setText(0, 5, "@[] !\"#$%&'()*+,-./1234567890:;<=>?|_");

      byte c = 0;
      for (int y = 0; y < 16; y++) {
        for (int x = 0; x < 16; x++, c++) {
          text.setCharset(false);
          text.setForegroundColor(C64Color.LIGHT_GREEN);
          text.setText(x, y + 8, c);
          text.setCharset(true);
          text.setForegroundColor(C64Color.LIGHT_RED);
          text.setText(x + 20, y + 8, c);
        }
      }

      stage.setScene(new Scene(text, text.getWidth(), text.getHeight()));
      stage.show();
    }
  }
}

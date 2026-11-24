package de.heiden.c64dt.gui.javafx;

import de.heiden.c64dt.bytes.ResourceLoader;
import de.heiden.c64dt.charset.C64Charset;
import de.heiden.c64dt.gui.C64Color;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.jspecify.annotations.Nullable;

import java.util.StringTokenizer;

/**
 * Component for displaying C64 text.
 */
public class C64TextArea extends C64Component {
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
  private @Nullable Color[][] _foregrounds;
  private @Nullable Color[][] _backgrounds;

  private boolean _upper;
  private C64Charset _charset;
  private final int[] _charsetROM;

  private static int @Nullable [] _defaultCharsetROM;

  //
  //
  //

  /**
   * Constructor.
   * The lower charset is used as default.
   *
   * @param columns Character columns to display.
   * @param rows Character rows to display.
   * @param factor Zoom factor.
   * @param resizable Is the backing image resizable?
   */
  public C64TextArea(int columns, int rows, double factor, boolean resizable) {
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
    _columns = (int) Math.ceil(getImageWidth() / (double) COLUMN_WIDTH);
    _rows = (int) Math.ceil(getImageHeight() / (double) ROW_HEIGHT);

    _chars = new byte[_rows][_columns];
    _foregrounds = new @Nullable Color[_rows][_columns];
    _backgrounds = new @Nullable Color[_rows][_columns];

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
   * Width of columns.
   */
  public int getColumnWidth() {
    return (int) Math.round(COLUMN_WIDTH * _factor);
  }

  /**
   * Set charset to use.
   *
   * @param upper Use upper case only charset?
   */
  public final void setCharset(boolean upper) {
    _upper = upper;
    _charset = C64Charset.charset(upper);
  }

  /**
   * Draw a string to the screen.
   *
   * @param column Column.
   * @param row Row.
   * @param s Characters.
   */
  public void setText(int column, int row, String s) {
    if (!s.contains("\n")) {
      // Optimization for single line text.
      setText(column, row, _charset.toBytes(s));
      return;
    }

    // Handle multi line text.
    var tokenizer = new StringTokenizer(s, "\n");
    for (var r = row; tokenizer.hasMoreTokens() && r < _rows; r++) {
      setText(column, r, tokenizer.nextToken());
    }
  }

  /**
   * Draw a string with inverted characters to the screen.
   *
   * @param column Column.
   * @param row Row.
   * @param s Characters.
   */
  public void setTextInverted(int column, int row, String s) {
    var characters = _charset.toBytes(s);
    for (var i = 0; i < characters.length; i++) {
      characters[i] |= 0x80;
    }
    setText(column, row, characters);
  }

  /**
   * Draw a string to the screen.
   *
   * @param column Column.
   * @param row Row.
   * @param s Characters in C64 encoding.
   */
  public void setText(int column, int row, byte... s) {
    var writer = getPixelWriter();
    for (int i = 0, c = column; i < s.length && c < _columns; i++, c++) {
      setTextInternal(c, row, s[i], writer);
    }
  }

  /**
   * Draw a single character to the screen.
   *
   * @param column Column.
   * @param row Row.
   * @param c Character in C64 encoding.
   */
  public void setText(int column, int row, byte c) {
    setTextInternal(column, row, c, getPixelWriter());
  }

  /**
   * Draw a single character to the screen.
   *
   * @param column Column.
   * @param row Row.
   * @param c Character in C64 encoding.
   * @param writer Pixel writer for backing image.
   */
  protected void setTextInternal(int column, int row, byte c, PixelWriter writer) {
    if (column >= _columns || row >= _rows) {
      // Early exit, when the text exceeds the buffer.
      // This may happen, e.g. when the component has been resized to a smaller size than the initial size.
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
    var space = _charset.toBytes(" ")[0];
    var foreground = getForegroundColor();
    var background = getBackgroundColor();
    for (var row = 0; row < _rows; row++) {
      var charRow = _chars[row];
      var foregroundRow = _foregrounds[row];
      var backgroundRow = _backgrounds[row];
      for (var column = 0; column < _columns; column++) {
        charRow[column] = space;
        foregroundRow[column] = foreground;
        backgroundRow[column] = background;
      }
    }

    restoreImage();
  }

  @Override
  protected void restoreImage() {
    var writer = getPixelWriter();
    for (var row = 0; row < _rows; row++) {
      for (var column = 0; column < _columns; column++) {
        paintCharacter(column, row, writer);
      }
    }
  }

  /**
   * Paint a single character onto the screen.
   *
   * @param column Column.
   * @param row Row.
   * @param writer Pixel writer for backing image.
   */
  private void paintCharacter(int column, int row, PixelWriter writer) {
    var foregroundColor = _foregrounds[row][column];
    if (foregroundColor == null) {
      return;
    }
    var backgroundColor = _backgrounds[row][column];
    if (backgroundColor == null) {
      return;
    }

    var charOffset = _upper ? 0x0000 : 0x0800;
    var charPtr = charOffset + (_chars[row][column] & 0xFF) * 8;
    for (int y = row * 8, lastY = y + 8; y < lastY; y++) {
      var data = _charsetROM[charPtr++];
      for (int x = column * 8, bit = 0x80; bit > 0; x++, bit = bit >> 1) {
        writer.setColor(x, y, (data & bit) != 0 ? foregroundColor : backgroundColor);
      }
    }
  }

  //
  // Test.
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
      var text = new C64TextArea(40, 25, 2, true);

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
      for (var y = 0; y < 16; y++) {
        for (var x = 0; x < 16; x++, c++) {
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

package de.heiden.c64dt.gui.swing;

import de.heiden.c64dt.bytes.ResourceLoader;
import de.heiden.c64dt.charset.C64Charset;
import de.heiden.c64dt.gui.C64Color;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.StringTokenizer;

/**
 * Component for displaying C64 text.
 */
public class JC64TextArea extends JC64CommonComponent {
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
    _columns = (int) Math.ceil(getImageWidth() / (double) COLUMN_WIDTH);
    _rows = (int) Math.ceil(getImageHeight() / (double) ROW_HEIGHT);

    _chars = new byte[_rows][_columns];
    _foregrounds = new Color[_rows][_columns];
    _backgrounds = new Color[_rows][_columns];
    clear();
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

    // handle multiline text
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
    for (int i = 0, c = column; i < s.length && c < _columns; i++, c++) {
      setTextInternal(c, row, s[i]);
    }
    repaint();
  }

  /**
   * Draw a single character to the screen.
   *
   * @param column column
   * @param row row
   * @param c character in C64 encoding
   */
  public void setText(int column, int row, byte c) {
    setTextInternal(column, row, c);
    repaint();
  }

  /**
   * Draw a single character to the screen.
   *
   * @param column column
   * @param row row
   * @param c character in C64 encoding
   */
  protected void setTextInternal(int column, int row, byte c) {
    if (column >= _columns || row >= _rows) {
      // early exit, when the text exceeds the buffer.
      // this may happen, e.g. when the component has been resized to a smaller size than the initial size.
      return;
    }

    _chars[row][column] = c;
    _foregrounds[row][column] = getForeground();
    _backgrounds[row][column] = getBackground();
  }

  /**
   * Clear component with background color.
   */
  public void clear() {
    byte space = _charset.toBytes(" ")[0];
    Color foreground = getForeground();
    Color background = getBackground();
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

    repaint();
  }

  /**
   * A repaint has been requested.
   * So the backing image will be updated.
   *
   * @param g graphics
   */
  @Override
  public void doPaintComponent(Graphics g) {
    for (int row = 0; row < _rows; row++) {
      for (int column = 0; column < _columns; column++) {
        paintCharacter(column, row);
      }
    }
    updateImageData();
  }

  /**
   * Paint a single character onto the screen.
   *
   * @param column column
   * @param row row
   */
  private void paintCharacter(int column, int row) {
    int[] imageData = getImageData();
    Color foregroundColor = _foregrounds[row][column];
    if (foregroundColor == null) {
      return;
    }
    Color backgroundColor = _backgrounds[row][column];
    if (backgroundColor == null) {
      return;
    }
    int foreground = foregroundColor.getRGB();
    int background = backgroundColor.getRGB();

    int charOffset = _upper ? 0x0000 : 0x0800;
    int charPtr = charOffset + (_chars[row][column] & 0xFF) * 8;
    for (int y = row * 8, lastY = y + 8; y < lastY; y++) {
      int offset = y * getImageWidth() + column * 8;
      int data = _charsetROM[charPtr++];
      for (int dx = 0, bit = 0x80; bit > 0; dx++, bit = bit >> 1) {
        imageData[offset + dx] = (data & bit) != 0 ? foreground : background;
      }
    }
  }

  //
  // test
  //

  public static void main(String[] args) {
    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    JC64TextArea text = new JC64TextArea(40, 25, 2, false);
    frame.add(text);

    frame.pack();
    frame.setVisible(true);

    text.setForeground(C64Color.LIGHT_BLUE);
    text.setBackground(C64Color.BLUE);
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
        text.setForeground(C64Color.LIGHT_GREEN);
        text.setText(x, y + 8, c);
        text.setCharset(true);
        text.setForeground(C64Color.LIGHT_RED);
        text.setText(x + 20, y + 8, c);
      }
    }

    // TODO 2010-03-07 mh: why is this repaint() needed???
    frame.repaint();
  }
}

package de.heiden.c64dt.gui.swing;

import de.heiden.c64dt.gui.C64Color;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Component for displaying C64 text lists.
 */
public class JC64List extends JList<String> {
  /**
   * Number of Rows.
   */
  private int _rows;

  /**
   * Number of columns.
   */
  private int _columns;

  /**
   * Scale factor for image.
   */
  private final double _factor;

  /**
   * Charset to use.
   */
  private boolean _upper;

  /**
   * Constructor.
   * The lower charset is used as default.
   *
   * @param columns Character columns to display.
   * @param rows Character rows to display.
   * @param factor Zoom factor.
   * @param resizable Is the backing image resizable?
   */
  public JC64List(int columns, int rows, final double factor, final boolean resizable) {
    _rows = rows;
    _columns = columns;
    _factor = factor;

    setPreferredSize(new Dimension(
      (int) Math.ceil(columns * JC64TextArea.COLUMN_WIDTH * factor),
      (int) Math.ceil(rows * JC64TextArea.ROW_HEIGHT * factor)));

    setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
      var columns1 = (int) Math.ceil(list.getWidth() / (JC64TextArea.COLUMN_WIDTH * factor));
      var rows1 = 1;

      var component = new JC64TextArea(columns1, rows1, factor, resizable);
      component.setBackground(getBackground());
      component.setForeground(getForeground());
      component.setCharset(_upper);

      var v = new StringBuilder(component.getColumns());
      v.append(value);
      for (int i = v.length(), e = component.getColumns(); i < e; i++) {
        v.append(" ");
      }

      if (isSelected) {
        component.setTextInverted(0, 0, v.toString());
      } else {
        component.setText(0, 0, v.toString());
      }

      return component;
    });

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
    _rows = (int) Math.ceil(getHeight() / (JC64TextArea.ROW_HEIGHT * _factor));
    _columns = (int) Math.ceil(getWidth() / (JC64TextArea.COLUMN_WIDTH * _factor));

    System.out.println("_rows = " + _rows);
    System.out.println("_columns = " + _columns);
  }

  /**
   * Number of character rows.
   */
  public int getRows() {
    return _rows;
  }

  /**
   * Number of character columns.
   */
  public int getColumns() {
    return _columns;
  }

  /**
   * Set charset to use.
   *
   * @param upper Use upper case only charset?
   */
  public final void setCharset(boolean upper) {
    _upper = upper;
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

  //
  // Test.
  //

  public static void main(String[] args) {
    var frame = new JFrame();
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    var model = new DefaultListModel<String>();
    model.add(0, "Test1 blah");
    model.add(1, "Test2");
    model.add(2, "Test3");

    var text = new JC64List(20, 3, 2, true);
    text.setModel(model);
    frame.add(text);

    frame.pack();
    frame.setVisible(true);

    // TODO 2010-03-07 mh: Why is this repaint() needed???
    frame.repaint();
  }
}

package de.heiden.c64dt.gui;

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
   * @param columns character columns to display
   * @param rows character rows to display
   * @param factor zoom factor
   * @param resizable Is the backing image resizable?
   */
  public JC64List(int columns, int rows, final double factor, final boolean resizable) {
    _rows = rows;
    _columns = columns;
    _factor = factor;

    setPreferredSize(new Dimension(
      (int) Math.ceil(columns * JC64TextArea.COLUMN_WIDTH * factor),
      (int) Math.ceil(rows * JC64TextArea.ROW_HEIGHT * factor)));

    setCellRenderer(new ListCellRenderer<String>() {
      @Override
      public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
        int columns = (int) Math.ceil(list.getWidth() / (JC64TextArea.COLUMN_WIDTH * factor));
        int rows = 1;

        JC64TextArea component = new JC64TextArea(columns, rows, factor, resizable);
        component.setBackground(getBackground());
        component.setForeground(getForeground());
        component.setCharset(_upper);

        StringBuilder v = new StringBuilder(component.getColumns());
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
      }
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
   * @param upper use upper case only charset?
   */
  public final void setCharset(boolean upper) {
    _upper = upper;
  }

  /**
   * Set foreground color.
   *
   * @param foreground foreground color index
   */
  public void setForeground(int foreground) {
    setForeground(SwingColors.COLOR[foreground]);
  }

  /**
   * Set foreground color.
   *
   * @param foreground foreground color
   */
  public void setForeground(C64Color foreground) {
    setForeground(foreground.ordinal());
  }

  /**
   * Set background color.
   *
   * @param background background color index
   */
  public void setBackground(int background) {
    setBackground(SwingColors.COLOR[background]);
  }

  /**
   * Set background color.
   *
   * @param background background color
   */
  public void setBackground(C64Color background) {
    setBackground(background.ordinal());
  }

  //
  // test
  //

  public static void main(String[] args) {
    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    DefaultListModel<String> model = new DefaultListModel<>();
    model.add(0, "Test1 blah");
    model.add(1, "Test2");
    model.add(2, "Test3");

    JC64List text = new JC64List(20, 3, 2, true);
    text.setModel(model);
    frame.add(text);

    frame.pack();
    frame.setVisible(true);

    // TODO 2010-03-07 mh: why is this repaint() needed???
    frame.repaint();
  }
}

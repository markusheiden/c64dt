package de.heiden.c64dt.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Component for displaying C64 text lists.
 */
public class JC64List extends JList<String> {
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
    this.setSize(new Dimension(
      (int) Math.ceil(columns * JC64TextArea.COLUMN_WIDTH * factor),
      (int) Math.ceil(rows * JC64TextArea.ROW_HEIGHT * factor)));

    setCellRenderer(new ListCellRenderer<String>() {
      @Override
      public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
        int columns = (int) Math.ceil(list.getWidth() / (JC64TextArea.COLUMN_WIDTH * factor));
        int rows = 1;

        JC64TextArea component = new JC64TextArea(columns, rows, factor, resizable);

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
  }

  //
  // test
  //

  public static void main(String[] args) {
    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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

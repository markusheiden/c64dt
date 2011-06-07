package de.heiden.c64dt.assembler.gui.util;

import javax.swing.*;
import java.awt.*;

/**
 * Static helpers for JTable.
 */
public class TableUtil
{

  /**
   * Get bounds for row +/- height rows.
   *
   * @param table Table
   * @param row Row
   * @param height Height
   */
  public static Rectangle getRowBounds(JTable table, int row, int height)
  {
    int first = Math.max(0, row - height);
    int last = Math.min(table.getRowCount() - 1, row + height);

    Rectangle result = table.getCellRect(first, -1, true);
    result = result.union(table.getCellRect(last, -1, true));
    Insets i = table.getInsets();

    result.x = i.left;
    result.width = table.getWidth() - i.left - i.right;

    return result;
  }

}

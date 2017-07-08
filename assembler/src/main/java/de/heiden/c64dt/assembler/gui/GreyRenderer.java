package de.heiden.c64dt.assembler.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Renders text with grey foreground color.
 */
public class GreyRenderer extends DefaultTableCellRenderer {
  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    Component result = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    result.setForeground(Color.GRAY);
    return result;
  }
}

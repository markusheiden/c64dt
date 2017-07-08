package de.heiden.c64dt.gui.swing;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

/**
 * Hex editor.
 */
public class JHexEditor extends JTable {
  public JHexEditor() {
    setTableHeader(null);
    byte[] bytes = new byte[256];
    for (int i = 0; i < bytes.length; i++) {
      bytes[i] = (byte) i;
    }
    setBytes(bytes);

    setShowGrid(false);
    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    setRowSelectionAllowed(false);
    setColumnSelectionAllowed(false);
    setCellSelectionEnabled(true);
    setDefaultRenderer(byte[].class, (table, value, isSelected, hasFocus, row, column) -> {
      byte[] text = (byte[]) value;
      JC64TextArea renderer = new JC64TextArea(text.length, 1, 2, false);
      if (isSelected) {
        renderer.setBackground(getSelectionBackground());
        renderer.setForeground(getSelectionForeground());
      } else {
        renderer.setBackground(getBackground());
        renderer.setForeground(getForeground());
      }
      renderer.setText(0, 0, text);
      return renderer;
    });
    setDefaultEditor(byte[].class, new TableCellEditor() {
      private final List<CellEditorListener> listeners = new ArrayList<>();

      @Override
      public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, final int row, final int column) {
        byte[] text = (byte[]) value;
        JC64TextArea editor = new JC64TextArea(text.length, 1, 2, false);
        if (isSelected) {
          editor.setBackground(getSelectionBackground());
          editor.setForeground(getSelectionForeground());
        } else {
          editor.setBackground(getBackground());
          editor.setForeground(getForeground());
        }
        editor.setText(0, 0, text);
        editor.addKeyListener(new KeyAdapter() {
          @Override
          public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
              JHexEditor.this.changeSelection(row + 1, column, false, false);
            } else if (e.getKeyCode() == KeyEvent.VK_UP) {
              JHexEditor.this.changeSelection(row - 1, column, false, false);
            } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
              JHexEditor.this.changeSelection(row, column - 1, false, false);
            } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
              JHexEditor.this.changeSelection(row, column + 1, false, false);
            } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
              JHexEditor.this.changeSelection(row - 1, column, false, false);
            } else {
              return;
            }

            e.consume();
          }
        });
        return editor;
      }

      @Override
      public Object getCellEditorValue() {
        return null;
      }

      @Override
      public boolean isCellEditable(EventObject anEvent) {
        return true;
      }

      @Override
      public boolean shouldSelectCell(EventObject anEvent) {
        return true;
      }

      @Override
      public boolean stopCellEditing() {
        for (CellEditorListener listener : new ArrayList<>(listeners)) {
          listener.editingStopped(new ChangeEvent(this));
        }
        return true;
      }

      @Override
      public void cancelCellEditing() {
        for (CellEditorListener listener : new ArrayList<>(listeners)) {
          listener.editingStopped(new ChangeEvent(this));
        }
      }

      @Override
      public void addCellEditorListener(CellEditorListener l) {
        listeners.add(l);
      }

      @Override
      public void removeCellEditorListener(CellEditorListener l) {
        listeners.remove(l);
      }
    });
  }

  public void setBytes(final byte[] bytes) {
    final int width = 16;
    setModel(new JHexEditorTableModel(bytes, width));
  }

  //
  // Specific model class
  //

  private static class JHexEditorTableModel extends AbstractTableModel {
    private final byte[] bytes;
    private final int width;

    public JHexEditorTableModel(byte[] bytes, int width) {
      this.bytes = bytes;
      this.width = width;
    }

    @Override
    public int getRowCount() {
      return (bytes.length + (width - 1)) / width;
    }

    @Override
    public int getColumnCount() {
      return 2;
    }

    @Override
    public String getColumnName(int columnIndex) {
      switch (columnIndex) {
        case 0:
          return "Hex";
        case 1:
          return "ASCII";
        default:
          throw new IllegalArgumentException("Wrong column index");
      }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
      switch (columnIndex) {
        case 0:
          return byte[].class;
        case 1:
          return byte[].class;
        default:
          throw new IllegalArgumentException("Wrong column index");
      }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
      return true;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      byte[] line = new byte[width];
      System.arraycopy(bytes, rowIndex * width, line, 0, width);
      switch (columnIndex) {
        case 0:
          return line;
        case 1:
          return line;
        default:
          throw new IllegalArgumentException("Wrong column index");
      }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    }
  }

  //
  // Test
  //

  public static void main(String[] args) {
    JHexEditor editor = new JHexEditor();
    editor.setPreferredSize(new Dimension(200, 200));

    JFrame frame = new JFrame();
    frame.getContentPane().add(editor);
    frame.pack();
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }
}

package de.heiden.c64dt.gui.swing.browser;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ListDataListener;

import de.heiden.c64dt.charset.C64Charset;
import de.heiden.c64dt.disk.IDirectory;
import de.heiden.c64dt.disk.IFile;
import de.heiden.c64dt.gui.swing.JC64TextArea;

import static com.github.cowwoc.requirements10.java.DefaultJavaValidators.requireThat;

/**
 * Directory.
 */
public class JDirectory extends JList<Object> {
  private static final int columns = 28;
  private int fontSize = 8;

  /**
   * Constructor.
   */
  public JDirectory() {
    setOpaque(true);
    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    Dimension size = new Dimension(16 * fontSize, 0);
    setPreferredSize(size);
    setMaximumSize(size);
    setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
      byte[] text;
      if (index == 0) {
        text = new byte[26];
        Arrays.fill(text, (byte) 0xA0);

        IDirectory directory = (IDirectory) value;
        byte[] name = directory.getName();
        byte[] idAndType = directory.getIdAndType();

        int i = 0;
        text[i++] = 0x30; // '0'
        text[i++] = 0x20; // ' '
        text[i++] = 0x22; // '"'
        System.arraycopy(name, 0, text, i, Math.max(16, name.length));
        text[i + name.length] = 0x22; // '"'
        i += 16 + 1;
        text[i++] = 0x20; // ' '
        System.arraycopy(idAndType, 0, text, i, Math.max(5, idAndType.length));
        i += 5;
        requireThat(i, "i").isEqualTo(text.length, "text.length");

        // show directory header inverted
        for (i = 2; i < text.length; i++) {
          text[i] |= 0x80;
        }
      } else {
        text = new byte[columns];
        Arrays.fill(text, (byte) 0x20);

        IFile file = (IFile) value;
        byte[] name = file.getName();

        int i = 0;
        writeInt(file.getSize(), text, i);
        i += 4;
        text[i++] = 0x20; // ' '
        text[i++] = 0x22; // '"'
        System.arraycopy(name, 0, text, i, Math.min(16, name.length));
        text[i + name.length] = 0x22; // '"'
        i += 16 + 1;
        text[i++] = 0x20; // ' '
        C64Charset.LOWER.toBytes(file.getMode().getType().toString(), text, i);
        i += 3;
        requireThat(i, "i").isEqualTo(text.length - 1, "text.length - 1");
        if (file.getMode().isLocked()) {
          text[i++] = 0x3C; // '<'
        }
      }

      JC64TextArea result = new JC64TextArea(text.length, 1, 2, false);
      if (isSelected) {
        result.setForeground(getSelectionForeground());
        result.setBackground(getSelectionBackground());
      } else {
        result.setForeground(getForeground());
        result.setBackground(getBackground());
      }
      result.setText(0, 0, text);

      requireThat(result, "result").isNotNull();
      return result;
    });
  }

  protected void writeInt(int size, byte[] text, int pos) {
    requireThat(size, "size").isGreaterThanOrEqualTo(0);
    requireThat(text, "text").isNotNull();

    if (size == 0) {
      text[pos] = 0x30;
    } else {
      int digits = ((int) Math.floor(Math.log10(size))) + 1;
      pos += digits;
      while (size != 0) {
        text[--pos] = (byte) (size % 10 + 0x30);
        size = size / 10;
      }
    }
  }

  /**
   * Set directory (model).
   *
   * @param directory directory
   */
  public void setDirectory(final IDirectory directory) {
    requireThat(directory, "directory").isNotNull();

    final List<IFile> files = directory.getFiles();
    files.removeIf(file -> !file.getMode().isVisible());

    setModel(new ListModel<>() {
      @Override
      public int getSize() {
        return files.size() + 1;
      }

      @Override
      public Object getElementAt(int index) {
        requireThat(index, "index").isLessThanOrEqualTo(directory.getFiles().size(), "directory.getFiles().size()");
        if (index == 0) {
          return directory;
        } else {
          return files.get(index - 1);
        }
      }

      @Override
      public void addListDataListener(ListDataListener l) {
      }

      @Override
      public void removeListDataListener(ListDataListener l) {
      }
    });

    Dimension size = new Dimension(columns * fontSize, (directory.getFiles().size() + 1) * fontSize);
    setPreferredSize(size);
    setMaximumSize(size);
    repaint();
  }
}

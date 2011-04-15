package de.heiden.c64dt.browser;

import de.heiden.c64dt.charset.C64Charset;
import de.heiden.c64dt.disk.IDirectory;
import de.heiden.c64dt.disk.IFile;
import de.heiden.c64dt.gui.JC64TextArea;
import org.springframework.util.Assert;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Directory.
 */
public class JDirectory extends JList
{
  private static final int columns = 28;
  private int fontSize = 8;

  /**
   * Constructor.
   */
  public JDirectory()
  {
    setOpaque(true);
    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    Dimension size = new Dimension(16 * fontSize, 0);
    setPreferredSize(size);
    setMaximumSize(size);
    setCellRenderer(new ListCellRenderer()
    {
      public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
      {
        byte[] text;
        if (index == 0)
        {
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
          Assert.isTrue(i == text.length, "Check: i == text.length");

          // show directory header inverted
          for (i = 2; i < text.length; i++)
          {
            text[i] |= 0x80;
          }
        }
        else
        {
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
          Assert.isTrue(i == text.length - 1, "Check: i == text.length - 1");
          if (file.getMode().isLocked())
          {
            text[i++] = 0x3C; // '<'
          }
        }

        JC64TextArea result = new JC64TextArea(text.length, 1, 2, false);
        if (isSelected)
        {
          result.setForeground(getSelectionForeground());
          result.setBackground(getSelectionBackground());
        }
        else
        {
          result.setForeground(getForeground());
          result.setBackground(getBackground());
        }
        result.setText(0, 0, text);

        Assert.notNull(result, "Postcondition: result != null");
        return result;
      }
    });
  }

  protected void writeInt(int size, byte[] text, int pos)
  {
    Assert.isTrue(size >= 0, "Precondition: getSize >= 0");
    Assert.notNull(text, "Precondition: text != null");

    if (size == 0)
    {
      text[pos] = 0x30;
    }
    else
    {
      int digits = ((int) Math.floor(Math.log10(size))) + 1;
      pos += digits;
      while (size != 0)
      {
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
  public void setDirectory(final IDirectory directory)
  {
    Assert.notNull(directory, "Precondition: directory != null");

    final List<IFile> files = directory.getFiles();
    for (Iterator<IFile> iter = files.iterator(); iter.hasNext();)
    {
      IFile file = iter.next();
      if (!file.getMode().isVisible())
      {
        iter.remove();
      }
    }

    setModel(new ListModel()
    {
      public int getSize()
      {
        return files.size() + 1;
      }

      public Object getElementAt(int index)
      {
        Assert.isTrue(index <= directory.getFiles().size(), "Precondition: index <= directory.getFiles().fontSize()");
        if (index == 0)
        {
          return directory;
        }
        else
        {
          return files.get(index - 1);
        }
      }

      public void addListDataListener(ListDataListener l)
      {
      }

      public void removeListDataListener(ListDataListener l)
      {
      }
    });

    Dimension size = new Dimension(columns * fontSize, (directory.getFiles().size() + 1) * fontSize);
    setPreferredSize(size);
    setMaximumSize(size);
    repaint();
  }
}

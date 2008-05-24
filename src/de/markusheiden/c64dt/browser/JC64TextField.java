package de.markusheiden.c64dt.browser;

import de.markusheiden.c64dt.util.ByteUtil;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * Text display for C64 encoded text using a C64 font.
 */
public class JC64TextField extends JComponent {
  private static byte[] charRom;
  private int fontSize = 8;
  private byte[] text;

  /**
   * Constructor.
   */
  public JC64TextField() {
    this(new byte[0]);
  }

  /**
   * Constructor.
   *
   * @param text text in C64 encoding
   */
  public JC64TextField(byte[] text) {
    Assert.notNull(text, "Precondition: text != null");

    this.text = text;

    init();

    setOpaque(true);
    setPreferredSize(new Dimension(text.length * fontSize , fontSize));
  }

  private void init() {
    if (charRom == null) {
      InputStream stream = getClass().getResourceAsStream("/roms/char.rom");
      Assert.notNull(stream, "Precondition: char.rom exists in roms folder");
      try {
        charRom = FileCopyUtils.copyToByteArray(stream);
      } catch (IOException e) {
        Assert.isTrue(false, "Precondition: Char rom is readable");
      }
    }
  }

  /**
   * Set displayed text.
   *
   * @param text
   */
  public void setText(byte[] text) {
    this.text = text;

    repaint();
  }

  @Override
  protected final void paintComponent(Graphics g) {
    g.setColor(getBackground());
    g.fillRect(0, 0, getWidth(), getHeight());

    g.setColor(getForeground());
    for (int i = 0, x= 0; i < text.length; i++, x += fontSize) {
      drawChar(g, x, 0, text[i]);
    }
  }

  protected final void drawChar(Graphics g, int x, int y, byte c) {
    int ptr = 8 * ByteUtil.toByte(c) + 8 * 256;
    for (int i = 0; i < 8; i++, y++) {
      drawLine(g, x, y, charRom[ptr++]);
    }
  }

  protected final void drawLine(Graphics g, int x, int y, byte image) {
    for (int i = 0; i < 8; i++, x++) {
      if ((image & 0x80) != 0) {
        g.fillRect(x, y, 1, 1);
      }
      image = (byte) (image << 1);
    }
  }
}

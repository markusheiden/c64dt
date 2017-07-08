package de.heiden.c64dt.gui.swing.browser;

import de.heiden.c64dt.charset.C64Charset;
import de.heiden.c64dt.disk.IDiskImage;
import de.heiden.c64dt.disk.SectorInputStream;
import de.heiden.c64dt.disk.d64.D64Reader;
import org.springframework.util.FileCopyUtils;

import javax.swing.*;
import java.io.*;

/**
 * Test.
 */
public class Test {
  public static void main(String[] args) throws Exception {
    BufferedReader r = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(new byte[]{0x01, 0x70, 0x02, 0x03}), C64Charset.UPPER));
    System.out.println(r.readLine());
    r.close();

    IDiskImage diskImage = new D64Reader().read(new File("./boulder_dash.d64"));
    BrowserGUI browser = new BrowserGUI();
    browser.setDiskImage(diskImage);
    InputStream is = new SectorInputStream(diskImage, diskImage.getDirectory().getFiles().get(5));
    byte[] boulderDash = FileCopyUtils.copyToByteArray(is);
    System.out.println("Length: " + boulderDash.length);
    FileCopyUtils.copy(boulderDash, new File("./dummy.prg"));

//    JaC64.main(new String[]{"doubleScreen", "1", "autostartPGM", "./dummy.prg", "soundOn", "1"});

    JFrame frame = new JFrame();
    frame.getContentPane().add(browser);
    frame.pack();
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }
}
package de.markusheiden.c64dt;

import de.markusheiden.c64dt.browser.BrowserGUI;
import de.markusheiden.c64dt.charset.C64Charset;
import de.markusheiden.c64dt.disk.IDiskImage;
import de.markusheiden.c64dt.disk.SectorInputStream;
import de.markusheiden.c64dt.disk.d64.D64Reader;
import org.springframework.util.FileCopyUtils;

import javax.swing.JFrame;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

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
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }
}

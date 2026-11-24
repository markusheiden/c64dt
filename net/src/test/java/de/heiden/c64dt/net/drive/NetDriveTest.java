package de.heiden.c64dt.net.drive;

import java.io.File;

/**
 * Test startup for net drive.
 */
public class NetDriveTest {
  public static void main(String[] args) throws Exception {
    var netDrive = new NetDrive(new File("."));
    netDrive.start();

    System.in.read();

    netDrive.stop();
  }
}

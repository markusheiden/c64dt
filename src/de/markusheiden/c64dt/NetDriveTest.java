package de.markusheiden.c64dt;

import de.markusheiden.c64dt.net.drive.NetDrive;

/**
 * Test startup for net drive.
 */
public class NetDriveTest {
  public static void main(String[] args) throws Exception {
    NetDrive netDrive = new NetDrive();
    netDrive.start();

    System.in.read();

    netDrive.stop();
  }
}
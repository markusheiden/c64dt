package de.heiden.c64dt.net.code;

import java.net.InetAddress;

/**
 * Manual executing some commands.
 */
public class ManualTest {
  public static void main(String[] args) throws Exception {
    var connection = new C64Connection(InetAddress.getByName("192.168.2.64"));
    connection.open();
    connection.ping();
    connection.close();
  }
}

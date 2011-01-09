package de.heiden.c64dt.net.code;

import java.net.InetAddress;

/**
 * Manual executing some commands.
 */
public class ManualTest
{
  public static void main(String[] args) throws Exception
  {
    C64Connection connection = new C64Connection(InetAddress.getByName("192.168.2.64"));
    connection.open();
    connection.write(0x0400, 0x33, 0x34, 0x35);
    connection.close();
  }
}

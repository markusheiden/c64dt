package de.heiden.c64dt;

import de.heiden.c64dt.assembler.ReassemblerView;

/**
 * Reassembler start class.
 */
public class Reassembler
{
  /**
   * Start reassembler gui.
   *
   * @param args arguments (currently not evaluated)
   */
  public static void main(String[] args)
  {
    // just for Mac OS...
    System.setProperty("com.apple.mrj.application.apple.menu.about.name", "C64 Reassembler");

    ReassemblerView gui = new ReassemblerView();
    gui.setVisible(true);
  }

}

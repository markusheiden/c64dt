package de.heiden.c64dt;

import de.heiden.c64dt.assembler.gui.ReassemblerView;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.swing.*;

/**
 * Reassembler start class.
 */
public class Reassembler {
  /**
   * Start reassembler gui.
   *
   * @param args arguments (currently not evaluated)
   */
  public static void main(String[] args) throws Exception {
    // Just for Mac OS...
    System.setProperty("apple.laf.useScreenMenuBar", "true");
    System.setProperty("com.apple.mrj.application.apple.menu.about.name", "C64 Reassembler");

    // Native look and feel
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("de.heiden.c64dt");
    ReassemblerView gui = context.getBean(ReassemblerView.class);
    gui.setVisible(true);
  }

}

package de.markusheiden.c64dt.assembler;

import org.springframework.util.Assert;

/**
 * Opcode address mode.
 */
public enum OpcodeMode {
  DIR(0), // direct
  IMM(1), // #$00
  ZP(1), // $00
  ZPX(1), // $00,X
  ZPY(1), // $00,Y
  IZX(1), // ($00,X)
  IZY(1), // ($00),Y
  ABS(2), // $0000
  ABX(2), // $0000,X
  ABY(2), // $0000,Y
  IND(2), // ($0000)
  REL(1); // $0000, PC-relative

  private int size;

  private OpcodeMode(int size) {
    Assert.isTrue(size >= 0 && size <= 2, "Precondition: size >= 0 && size <= 2");

    this.size = size;
  }

  public int getSize() {
    return size;
  }
}

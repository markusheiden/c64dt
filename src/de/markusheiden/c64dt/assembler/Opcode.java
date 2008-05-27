package de.markusheiden.c64dt.assembler;

import static de.markusheiden.c64dt.assembler.OpcodeType.*;
import static de.markusheiden.c64dt.assembler.OpcodeMode.*;
import org.springframework.util.Assert;

/**
 * 6502 series opcodes.
 */
public enum Opcode {
  OPCODE_00(0x00, REG, BRK, DIR, 7),
  OPCODE_01(0x01, REG, ORA, IZX, 6),
  OPCODE_02(0x02, ILL, KIL, DIR, 0),
  OPCODE_03(0x03, ILL, SLO, IZX, 8),
  OPCODE_04(0x04, ILL, NOP, ZP, 3),
  OPCODE_05(0x05, REG, ORA, ZP, 3),
  OPCODE_06(0x06, REG, ASL, ZP, 5),
  OPCODE_07(0x07, ILL, SLO, ZP, 5),
  OPCODE_08(0x08, REG, PHP, DIR, 3),
  OPCODE_09(0x09, REG, ORA, IMM, 2),
  OPCODE_0A(0x0A, REG, ASL, DIR, 2),
  OPCODE_0B(0x0B, ILL, ANC, IMM, 2),
  OPCODE_0C(0x0C, ILL, NOP, ABS, 4),
  OPCODE_0D(0x0D, REG, ORA, ABS, 4),
  OPCODE_0E(0x0E, REG, ASL, ABS, 6),
  OPCODE_0F(0x0F, ILL, SLO, ABS, 6),

  OPCODE_10(0x10, REG, BPL, REL, 2),
  OPCODE_11(0x11, REG, ORA, IZY, 5),
  OPCODE_12(0x12, ILL, KIL, DIR, 0),
  OPCODE_13(0x13, ILL, SLO, IZY, 8),
  OPCODE_14(0x14, ILL, NOP, ZPX, 4),
  OPCODE_15(0x15, REG, ORA, ZPX, 4),
  OPCODE_16(0x16, REG, ASL, ZPX, 6),
  OPCODE_17(0x17, ILL, SLO, ZPX, 6),
  OPCODE_18(0x18, REG, CLC, DIR, 2),
  OPCODE_19(0x19, REG, ORA, ABY, 4),
  OPCODE_1A(0x1A, ILL, NOP, DIR, 2),
  OPCODE_1B(0x1B, ILL, SLO, ABY, 7),
  OPCODE_1C(0x1C, ILL, NOP, ABX, 4),
  OPCODE_1D(0x1D, REG, ORA, ABX, 4),
  OPCODE_1E(0x1E, REG, ASL, ABX, 7),
  OPCODE_1F(0x1F, ILL, SLO, ABX, 7),

  OPCODE_20(0x20, REG, JSR, ABS, 6),
  OPCODE_21(0x21, REG, AND, IZX, 6),
  OPCODE_22(0x22, ILL, KIL, DIR, 0),
  OPCODE_23(0x23, ILL, RLA, IZX, 8),
  OPCODE_24(0x24, REG, BIT, ZP, 3),
  OPCODE_25(0x25, REG, AND, ZP, 3),
  OPCODE_26(0x26, REG, ROL, ZP, 5),
  OPCODE_27(0x27, ILL, RLA, ZP, 5),
  OPCODE_28(0x28, REG, PLP, DIR, 4),
  OPCODE_29(0x29, REG, AND, IMM, 2),
  OPCODE_2A(0x2A, REG, ROL, DIR, 2),
  OPCODE_2B(0x2B, ILL, ANC, IMM, 2),
  OPCODE_2C(0x2C, REG, BIT, ABS, 4),
  OPCODE_2D(0x2D, REG, AND, ABS, 4),
  OPCODE_2E(0x2E, REG, ROL, ABS, 6),
  OPCODE_2F(0x2F, ILL, ROL, ABS, 6),

  OPCODE_30(0x30, REG, BMI, REL, 2),
  OPCODE_31(0x31, REG, AND, IZX, 6),
  OPCODE_32(0x32, ILL, KIL, DIR, 0),
  OPCODE_33(0x33, ILL, RLA, IZY, 8),
  OPCODE_34(0x34, ILL, NOP, ZPX, 4),
  OPCODE_35(0x35, REG, AND, ZPX, 4),
  OPCODE_36(0x36, REG, ROL, ZPX, 6),
  OPCODE_37(0x37, ILL, RLA, ZPX, 6),
  OPCODE_38(0x38, REG, SEC, DIR, 2),
  OPCODE_39(0x39, REG, AND, ABY, 4),
  OPCODE_3A(0x3A, ILL, NOP, DIR, 2),
  OPCODE_3B(0x3B, ILL, RLA, ABY, 7),
  OPCODE_3C(0x3C, ILL, NOP, ABX, 4),
  OPCODE_3D(0x3D, REG, AND, ABX, 4),
  OPCODE_3E(0x3E, REG, ROL, ABX, 7),
  OPCODE_3F(0x3F, ILL, RLA, ABX, 7),

  OPCODE_40(0x40, REG, RTI, DIR, 6),
  OPCODE_41(0x41, REG, EOR, IZX, 6),
  OPCODE_42(0x42, ILL, KIL, DIR, 0),
  OPCODE_43(0x43, ILL, SRE, IZX, 8),
  OPCODE_44(0x44, ILL, NOP, ZP, 3),
  OPCODE_45(0x45, REG, EOR, ZP, 3),
  OPCODE_46(0x46, REG, LSR, ZP, 5),
  OPCODE_47(0x47, ILL, SRE, ZP, 5),
  OPCODE_48(0x48, REG, PHA, DIR, 3),
  OPCODE_49(0x49, REG, EOR, IMM, 2),
  OPCODE_4A(0x4A, REG, LSR, DIR, 2),
  OPCODE_4B(0x4B, ILL, ALR, IMM, 2),
  OPCODE_4C(0x4C, REG, JMP, ABS, 3),
  OPCODE_4D(0x4D, REG, EOR, ABS, 4),
  OPCODE_4E(0x4E, REG, LSR, ABS, 6),
  OPCODE_4F(0x4F, ILL, SRE, ABS, 6),

  OPCODE_50(0x50, REG, BVC, REL, 2),
  OPCODE_51(0x51, REG, EOR, IZY, 5),
  OPCODE_52(0x52, ILL, KIL, DIR, 0),
  OPCODE_53(0x53, ILL, SRE, IZY, 8),
  OPCODE_54(0x54, ILL, NOP, ZPX, 4),
  OPCODE_55(0x55, REG, EOR, ZPX, 4),
  OPCODE_56(0x56, REG, LSR, ZPX, 6),
  OPCODE_57(0x57, ILL, SRE, ZPX, 6),
  OPCODE_58(0x58, REG, CLI, DIR, 2),
  OPCODE_59(0x59, REG, EOR, ABY, 4),
  OPCODE_5A(0x5A, ILL, NOP, DIR, 2),
  OPCODE_5B(0x5B, ILL, SRE, ABY, 7),
  OPCODE_5C(0x5C, ILL, NOP, ABX, 4),
  OPCODE_5D(0x5D, REG, EOR, ABX, 4),
  OPCODE_5E(0x5E, REG, LSR, ABX, 7),
  OPCODE_5F(0x5F, ILL, SRE, ABX, 7),

  OPCODE_60(0x60, REG, RTS, DIR, 6),
  OPCODE_61(0x61, REG, ADC, IZX, 6),
  OPCODE_62(0x62, ILL, KIL, DIR, 0),
  OPCODE_63(0x63, ILL, RRA, IZX, 8),
  OPCODE_64(0x64, ILL, NOP, ZP, 3),
  OPCODE_65(0x65, REG, ADC, ZP, 3),
  OPCODE_66(0x66, REG, ROR, ZP, 5),
  OPCODE_67(0x67, ILL, RRA, ZP, 5),
  OPCODE_68(0x68, REG, PLA, DIR, 4),
  OPCODE_69(0x69, REG, ADC, IMM, 2),
  OPCODE_6A(0x6A, REG, ROR, DIR, 2),
  OPCODE_6B(0x6B, ILL, ARR, IMM, 2),
  OPCODE_6C(0x6C, REG, JMP, IND, 5),
  OPCODE_6D(0x6D, REG, ADC, ABS, 4),
  OPCODE_6E(0x6E, REG, ROR, ABS, 6),
  OPCODE_6F(0x6F, ILL, RRA, ABS, 6),

  OPCODE_70(0x70, REG, BVC, REL, 2),
  OPCODE_71(0x71, REG, ADC, IZY, 5),
  OPCODE_72(0x72, ILL, KIL, DIR, 0),
  OPCODE_73(0x73, ILL, RRA, IZY, 8),
  OPCODE_74(0x74, ILL, NOP, ZPX, 4),
  OPCODE_75(0x75, REG, ADC, ZPX, 4),
  OPCODE_76(0x76, REG, ROR, ZPX, 6),
  OPCODE_77(0x77, ILL, RRA, ZPX, 6),
  OPCODE_78(0x78, REG, SEI, DIR, 2),
  OPCODE_79(0x79, REG, ADC, ABY, 4),
  OPCODE_7A(0x7A, ILL, NOP, DIR, 2),
  OPCODE_7B(0x7B, ILL, RRA, ABY, 7),
  OPCODE_7C(0x7C, ILL, NOP, ABX, 4),
  OPCODE_7D(0x7D, REG, ADC, ABX, 4),
  OPCODE_7E(0x7E, REG, ROR, ABX, 7),
  OPCODE_7F(0x7F, ILL, RRA, ABX, 7),

  OPCODE_80(0x80, ILL, NOP, IMM, 2),
  OPCODE_81(0x81, REG, STA, IZX, 6),
  OPCODE_82(0x82, ILL, NOP, IMM, 2),
  OPCODE_83(0x83, ILL, SAX, IZX, 6),
  OPCODE_84(0x84, REG, STY, ZP, 3),
  OPCODE_85(0x85, REG, STA, ZP, 3),
  OPCODE_86(0x86, REG, STX, ZP, 3),
  OPCODE_87(0x87, ILL, SAX, ZPY, 4),
  OPCODE_88(0x88, REG, DEY, DIR, 2),
  OPCODE_89(0x89, ILL, NOP, IMM, 2),
  OPCODE_8A(0x8A, REG, TXA, DIR, 2),
  OPCODE_8B(0x8B, ILL, XAA, IMM, 2),
  OPCODE_8C(0x8C, REG, STY, ABS, 4),
  OPCODE_8D(0x8D, REG, STA, ABS, 4),
  OPCODE_8E(0x8E, REG, STX, ABS, 4),
  OPCODE_8F(0x8F, ILL, SAX, ABS, 4),

  OPCODE_90(0x90, REG, BCC, REL, 2),
  OPCODE_91(0x91, REG, STA, IZY, 6),
  OPCODE_92(0x92, ILL, KIL, DIR, 0),
  OPCODE_93(0x93, ILL, AHX, IZY, 6),
  OPCODE_94(0x94, REG, STY, ZPX, 4),
  OPCODE_95(0x95, REG, STA, ZPX, 4),
  OPCODE_96(0x96, REG, STX, ZPY, 4),
  OPCODE_97(0x97, ILL, SAX, ZPX, 4),
  OPCODE_98(0x98, REG, TYA, DIR, 2),
  OPCODE_99(0x99, REG, STA, ABY, 5),
  OPCODE_9A(0x9A, REG, TXS, DIR, 2),
  OPCODE_9B(0x9B, ILL, TAS, ABY, 5),
  OPCODE_9C(0x9C, ILL, SHY, ABX, 5),
  OPCODE_9D(0x9D, REG, STA, ABX, 5),
  OPCODE_9E(0x9E, ILL, SHX, ABY, 5),
  OPCODE_9F(0x9F, ILL, AHX, ABY, 5),

  OPCODE_A0(0xA0, REG, LDY, IMM, 2),
  OPCODE_A1(0xA1, REG, LDA, IZX, 6),
  OPCODE_A2(0xA2, REG, LDX, IMM, 2),
  OPCODE_A3(0xA3, ILL, LAX, IZX, 6),
  OPCODE_A4(0xA4, REG, null, null, 0),
  OPCODE_A5(0xA5, REG, null, null, 0),
  OPCODE_A6(0xA6, REG, null, null, 0),
  OPCODE_A7(0xA7, ILL, null, null, 0),
  OPCODE_A8(0xA8, REG, null, null, 0),
  OPCODE_A9(0xA9, REG, null, null, 0),
  OPCODE_AA(0xAA, REG, null, null, 0),
  OPCODE_AB(0xAB, ILL, null, null, 0),
  OPCODE_AC(0xAC, REG, null, null, 0),
  OPCODE_AD(0xAD, REG, null, null, 0),
  OPCODE_AE(0xAE, REG, null, null, 0),
  OPCODE_AF(0xAF, ILL, null, null, 0),

  OPCODE_B0(0xB0, REG, null, null, 0),
  OPCODE_B1(0xB1, REG, null, null, 0),
  OPCODE_B2(0xB2, ILL, KIL, DIR, 0),
  OPCODE_B3(0xB3, ILL, null, null, 0),
  OPCODE_B4(0xB4, REG, null, null, 0),
  OPCODE_B5(0xB5, REG, null, null, 0),
  OPCODE_B6(0xB6, REG, null, null, 0),
  OPCODE_B7(0xB7, ILL, null, null, 0),
  OPCODE_B8(0xB8, REG, null, null, 0),
  OPCODE_B9(0xB9, REG, null, null, 0),
  OPCODE_BA(0xBA, REG, null, null, 0),
  OPCODE_BB(0xBB, ILL, null, null, 0),
  OPCODE_BC(0xBC, REG, null, null, 0),
  OPCODE_BD(0xBD, REG, null, null, 0),
  OPCODE_BE(0xBE, REG, null, null, 0),
  OPCODE_BF(0xBF, ILL, null, null, 0),

  OPCODE_C0(0xC0, REG, null, null, 0),
  OPCODE_C1(0xC1, REG, null, null, 0),
  OPCODE_C2(0xC2, ILL, null, null, 0),
  OPCODE_C3(0xC3, ILL, null, null, 0),
  OPCODE_C4(0xC4, REG, null, null, 0),
  OPCODE_C5(0xC5, REG, null, null, 0),
  OPCODE_C6(0xC6, REG, null, null, 0),
  OPCODE_C7(0xC7, ILL, null, null, 0),
  OPCODE_C8(0xC8, REG, null, null, 0),
  OPCODE_C9(0xC9, REG, null, null, 0),
  OPCODE_CA(0xCA, REG, null, null, 0),
  OPCODE_CB(0xCB, ILL, null, null, 0),
  OPCODE_CC(0xCC, REG, null, null, 0),
  OPCODE_CD(0xCD, REG, null, null, 0),
  OPCODE_CE(0xCE, REG, null, null, 0),
  OPCODE_CF(0xCF, ILL, null, null, 0),

  OPCODE_D0(0xD0, REG, null, null, 0),
  OPCODE_D1(0xD1, REG, null, null, 0),
  OPCODE_D2(0xD2, ILL, KIL, DIR, 0),
  OPCODE_D3(0xD3, ILL, null, null, 0),
  OPCODE_D4(0xD4, ILL, null, null, 0),
  OPCODE_D5(0xD5, REG, null, null, 0),
  OPCODE_D6(0xD6, REG, null, null, 0),
  OPCODE_D7(0xD7, ILL, null, null, 0),
  OPCODE_D8(0xD8, REG, null, null, 0),
  OPCODE_D9(0xD9, REG, null, null, 0),
  OPCODE_DA(0xDA, ILL, null, null, 0),
  OPCODE_DB(0xDB, ILL, null, null, 0),
  OPCODE_DC(0xDC, ILL, null, null, 0),
  OPCODE_DD(0xDD, REG, null, null, 0),
  OPCODE_DE(0xDE, REG, null, null, 0),
  OPCODE_DF(0xDF, ILL, null, null, 0),

  OPCODE_E0(0xE0, REG, null, null, 0),
  OPCODE_E1(0xE1, REG, null, null, 0),
  OPCODE_E2(0xE2, ILL, null, null, 0),
  OPCODE_E3(0xE3, ILL, null, null, 0),
  OPCODE_E4(0xE4, REG, null, null, 0),
  OPCODE_E5(0xE5, REG, null, null, 0),
  OPCODE_E6(0xE6, REG, null, null, 0),
  OPCODE_E7(0xE7, ILL, null, null, 0),
  OPCODE_E8(0xE8, REG, null, null, 0),
  OPCODE_E9(0xE9, REG, null, null, 0),
  OPCODE_EA(0xEA, REG, null, null, 0),
  OPCODE_EB(0xEB, ILL, null, null, 0),
  OPCODE_EC(0xEC, REG, null, null, 0),
  OPCODE_ED(0xED, REG, null, null, 0),
  OPCODE_EE(0xEE, REG, null, null, 0),
  OPCODE_EF(0xEF, ILL, null, null, 0),

  OPCODE_F0(0xF0, REG, null, null, 0),
  OPCODE_F1(0xF1, REG, null, null, 0),
  OPCODE_F2(0xF2, ILL, KIL, DIR, 0),
  OPCODE_F3(0xF3, ILL, null, null, 0),
  OPCODE_F4(0xF4, ILL, null, null, 0),
  OPCODE_F5(0xF5, REG, null, null, 0),
  OPCODE_F6(0xF6, REG, null, null, 0),
  OPCODE_F7(0xF7, ILL, null, null, 0),
  OPCODE_F8(0xF8, REG, null, null, 0),
  OPCODE_F9(0xF9, REG, null, null, 0),
  OPCODE_FA(0xFA, ILL, null, null, 0),
  OPCODE_FB(0xFB, ILL, null, null, 0),
  OPCODE_FC(0xFC, ILL, null, null, 0),
  OPCODE_FD(0xFD, REG, null, null, 0),
  OPCODE_FE(0xFE, REG, null, null, 0),
  OPCODE_FF(0xFF, ILL, null, null, 0);

  private int opcode;
  private boolean legal;
  private OpcodeType type;
  private OpcodeMode mode;
  private int cycles;

  private Opcode(int opcode, boolean legal, OpcodeType type, OpcodeMode mode, int cycles) {
    Assert.isTrue(opcode >= 0x00 && opcode <= 0xFF, "Precondition: opcode >= 0x00 && opcode <= 0xFF");
    Assert.notNull(type, "Precondition: type != null");
    Assert.notNull(mode, "Precondition: mode != null");
    Assert.isTrue(type == KIL || cycles >= 0, "Precondition: type == KIL || cycles >= 0");
    Assert.isTrue(cycles <= 8, "Precondition: cycles <= 8");

    this.opcode = opcode;
    this.legal = legal;
    this.type = type;
    this.mode = mode;
    this.cycles = cycles;
  }

  public int getOpcode() {
    return opcode;
  }

  public boolean isLegal() {
    return legal;
  }

  public OpcodeType getType() {
    return type;
  }

  public OpcodeMode getMode() {
    return mode;
  }

  public int getCycles() {
    return cycles;
  }
}
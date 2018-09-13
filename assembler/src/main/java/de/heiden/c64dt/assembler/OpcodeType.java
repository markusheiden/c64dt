package de.heiden.c64dt.assembler;

/**
 * Opcode types.
 */
public enum OpcodeType {
  // logical/arithmetic commands
  ORA(false, false),
  AND(false, false),
  EOR(false, false),
  ADC(false, false),
  SBC(false, false),
  CMP(false, false),
  CPX(false, false),
  CPY(false, false),
  DEC(false, false),
  DEX(false, false),
  DEY(false, false),
  INC(false, false),
  INX(false, false),
  INY(false, false),
  ASL(false, false),
  ROL(false, false),
  LSR(false, false),
  ROR(false, false),

  // move commands
  LDA(false, false),
  STA(false, false),
  LDX(false, false),
  STX(false, false),
  LDY(false, false),
  STY(false, false),
  TAX(false, false),
  TXA(false, false),
  TAY(false, false),
  TYA(false, false),
  TSX(false, false),
  TXS(false, false),
  PLA(false, false),
  PHA(false, false),
  PLP(false, false),
  PHP(false, false),

  // jump/flag commands
  BPL(true, false),
  BMI(true, false),
  BVC(true, false),
  BVS(true, false),
  BCC(true, false),
  BCS(true, false),
  BNE(true, false),
  BEQ(true, false),
  BRK(false, true),
  RTI(false, true),
  JSR(true, false),
  RTS(false, true),
  JMP(true, true),
  BIT(false, false),
  CLC(false, false),
  SEC(false, false),
  CLD(false, false),
  SED(false, false),
  CLI(false, false),
  SEI(false, false),
  CLV(false, false),
  NOP(false, false),

  // illegal opcodes
  SLO(false, false),
  RLA(false, false),
  SRE(false, false),
  RRA(false, false),
  SAX(false, false),
  LAX(false, false),
  DCP(false, false),
  ISC(false, false),
  ANC(false, false),
  ALR(false, false),
  ARR(false, false),
  XAA(false, false),
  AXS(false, false),
  AHX(false, false),
  SHY(false, false),
  SHX(false, false),
  TAS(false, false),
  LAS(false, false),

  // non-functional opcodes
  KIL(false, false);

  public static final boolean REG = true;
  public static final boolean ILL = false;

  private final boolean jump;
  private final boolean end;

  /**
   * Is the address of this opcode a jump/branch destination?.
   */
  public boolean isJump() {
    return jump;
  }

  /**
   * Is the opcode right after this opcode not reachable from this opcode?
   */
  public boolean isEnd() {
    return end;
  }

  /**
   * Constructor.
   *
   * @param jump is the address a jump/branch destination?
   * @param end is the opcode right after this opcode not reachable from this opcode?
   */
  OpcodeType(boolean jump, boolean end) {
    this.jump = jump;
    this.end = end;
  }
}

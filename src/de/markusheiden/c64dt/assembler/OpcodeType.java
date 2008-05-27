package de.markusheiden.c64dt.assembler;

/**
 * Opcode types.
 */
public enum OpcodeType {
  // logical/arithmetic commands
  ORA,
  AND,
  EOR,
  ADC,
  SBC,
  CMP,
  CPX,
  CPY,
  DEC,
  DEX,
  DEY,
  INC,
  INX,
  INY,
  ASL,
  ROL,
  LSR,
  ROR,

  // move commands
  LDA,
  STA,
  LDX,
  STX,
  LDY,
  STY,
  TAX,
  TXA,
  TAY,
  TYA,
  TSX,
  TXS,
  PLA,
  PHA,
  PLP,
  PHP,

  // jump/flag commands
  BPL,
  BMI,
  BVC,
  BVS,
  BCC,
  BCS,
  BNE,
  BEQ,
  BRK,
  RTI,
  JSR,
  RTS,
  JMP,
  BIT,
  CLC,
  SEC,
  CLD,
  SED,
  CLI,
  SEI,
  CLV,
  NOP,

  // illegal opcodes
  SLO,
  RLA,
  SRE,
  RRA,
  SAX,
  LAX,
  DCP,
  ISC,
  ANC,
  ALR,
  ARR,
  XAA,
  AXS,
  AHX,
  SHY,
  SHX,
  TAS,
  LAS,

  // non-functional opcodes
  KIL;

  public static final boolean REG = true;
  public static final boolean ILL = false;
}

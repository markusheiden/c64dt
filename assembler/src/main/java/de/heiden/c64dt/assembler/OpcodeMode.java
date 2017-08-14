package de.heiden.c64dt.assembler;

import static de.heiden.c64dt.bytes.HexUtil.hexByte;
import static de.heiden.c64dt.bytes.HexUtil.hexWord;
import static org.bitbucket.cowwoc.requirements.core.Requirements.requireThat;

/**
 * Opcode address mode.
 */
public enum OpcodeMode {
  // direct
  DIR(0, false) {
    @Override
    public String toString(int pc, int argument) {
      return "";
    }

    @Override
    public String toString(String argument) {
      return "";
    }
  },

  // #$00
  IMM(1, false) {
    @Override
    public String toString(String argument) {
      return "#" + argument;
    }
  },

  // $00
  ZPD(1, true) {
    @Override
    public String toString(String argument) {
      return argument;
    }
  },

  // $00,X
  ZPX(1, true) {
    @Override
    public String toString(String argument) {
      return argument + ",X";
    }
  },

  // $00,Y
  ZPY(1, true) {
    @Override
    public String toString(String argument) {
      return argument + ",Y";
    }
  },

  // ($00,X)
  IZX(1, true) {
    @Override
    public String toString(String argument) {
      return "(" + argument + ",X)";
    }
  },

  // ($00),Y
  IZY(1, true) {
    @Override
    public String toString(String argument) {
      return "(" + argument + "),Y";
    }
  },

  // $0000
  ABS(2, true) {
    @Override
    public String toString(String argument) {
      return argument;
    }
  },

  // $0000,X
  ABX(2, true) {
    @Override
    public String toString(String argument) {
      return argument + ",X";
    }
  },

  // $0000,Y
  ABY(2, true) {
    @Override
    public String toString(String argument) {
      return argument + ",Y";
    }
  },

  // ($0000)
  IND(2, true) {
    @Override
    public String toString(String argument) {
      return "(" + argument + ")";
    }
  },

  // $0000, PC-relative
  REL(1, true) {
    @Override
    public int getAddress(int pc, int argument) {
      // argument is a signed byte
      return (pc + 2 + (byte) argument) & 0xFFFF;
    }

    @Override
    public String toString(int pc, int argument) {
      return toString(hexWord(getAddress(pc, argument)));
    }

    @Override
    public String toString(String argument) {
      return argument;
    }
  };

  private final int size;
  private final boolean isAddress;

  /**
   * Number of bytes this address mode uses.
   */
  public final int getSize() {
    requireThat("size", size).isGreaterThanOrEqualTo(0).isLessThanOrEqualTo(2);
    return size;
  }

  /**
   * Does this address mode use an address?.
   */
  public boolean isAddress() {
    return isAddress;
  }

  /**
   * Does this address mode have an argument?.
   */
  public final boolean hasArgument() {
    return size != 0;
  }

  /**
   * Compute absolute address.
   *
   * @param pc Program counter
   */
  public int getAddress(int pc, int argument) {
    requireThat("isAddress()", isAddress()).isTrue();

    return argument;
  }

  /**
   * String representation for this address mode with a given argument.
   *
   * @param pc address of opcode
   * @param argument argument of opcode
   */
  public String toString(int pc, int argument) {
    // Default implementation, will be overridden by some modes
    return toString(getSize() == 1 ? hexByte(argument) : hexWord(argument));
  }

  /**
   * String representation for this address mode with a given (generic) argument.
   * This method is used for reassembling, if the argument is label.
   *
   * @param argument argument
   */
  public abstract String toString(String argument);

  /**
   * Constructor.
   *
   * @param size number of bytes the argument takes
   * @param isAddress is the argument a (non zero page) address?
   */
  OpcodeMode(int size, boolean isAddress) {
    requireThat("size", size).isGreaterThanOrEqualTo(0).isLessThanOrEqualTo(2);

    this.size = size;
    this.isAddress = isAddress;
  }
}

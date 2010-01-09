package de.heiden.c64dt.assembler;

import org.springframework.util.Assert;

import static de.heiden.c64dt.util.HexUtil.format2;
import static de.heiden.c64dt.util.HexUtil.format4;

/**
 * Opcode address mode.
 */
public enum OpcodeMode {
  // direct
  DIR(0, false) {
    public String toString(String argument) {
      return "";
    }
  },

  // #$00
  IMM(1, false) {
    public String toString(String argument) {
      return "#" + argument;
    }
  },

  // $00
  ZPD(1, true) {
    public String toString(String argument) {
      return argument;
    }
  },

  // $00,X
  ZPX(1, true) {
    public String toString(String argument) {
      return argument + ",X";
    }
  },

  // $00,Y
  ZPY(1, true) {
    public String toString(String argument) {
      return argument + ",Y";
    }
  },

  // ($00,X)
  IZX(1, true) {
    public String toString(String argument) {
      return "(" + argument + ",X)";
    }
  },

  // ($00),Y
  IZY(1, true) {
    public String toString(String argument) {
      return "(" + argument + "),Y";
    }
  },

  // $0000
  ABS(2, true) {
    public String toString(String argument) {
      return argument;
    }
  },

  // $0000,X
  ABX(2, true) {
    public String toString(String argument) {
      return argument + ",X";
    }
  },

  // $0000,Y
  ABY(2, true) {
    public String toString(String argument) {
      return argument + ",Y";
    }
  },

  // ($0000)
  IND(2, true) {
    public String toString(String argument) {
      return "(" + argument + ")";
    }
  },

  // $0000, PC-relative
  REL(1, true) {
    public String toString(String argument) {
      return argument;
    }

    public String toString(int argument) {
      // display target address as word
      return toString("$" + format4(argument));
    }
  };

  private final int size;
  private final boolean isAddress;

  /**
   * Number of bytes this address mode uses.
   */
  public int getSize() {
    Assert.isTrue(size >= 0 && size <= 2, "Postcondition: result >= 0 && result <= 2");
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
   * String representation for this address mode with a given numeric argument.
   *
   * @param argument argument
   */
  public String toString(int argument) {
    Assert.isTrue(hasArgument(), "Precondition: hasArgument()");
    return toString("$" + (size == 1? format2(argument) : format4(argument)));
  }

  /**
   * String representation for this address mode with a given (generic) argument.
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
  private OpcodeMode(int size, boolean isAddress) {
    Assert.isTrue(size >= 0 && size <= 2, "Precondition: getSize >= 0 && getSize <= 2");

    this.size = size;
    this.isAddress = isAddress;
  }
}

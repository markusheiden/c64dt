package de.heiden.c64dt.disk;

import de.heiden.c64dt.bytes.HexUtil;

import java.util.Arrays;

/**
 * GCR codec.
 * <p/>
 * GCR 5 to 4 scheme:
 * 11111222 22333334 44445555 56666677 77788888
 */
public class GCR {
  public static final int[] GCR = {
    0x0A, 0x0B, 0x12, 0x13,
    0x0E, 0x0F, 0x16, 0x17,
    0x09, 0x19, 0x1A, 0x1B,
    0x0D, 0x1D, 0x1E, 0x15
  };

  public static void main(String[] args) {
    new GCR().start();
  }

  private void start() {
    int[] result = calculate();
    if (result == null) {
      System.out.println("no match");
      return;
    }

    System.out.println("length = " + HexUtil.hexWord(length(result)));
    System.out.println(toString(result));
  }

  private int[] calculate() {
    int[][] nibbles = new int[][]
      {
        generate(0), // nibble1
        generate(5), // nibble2
        generate(2), // nibble3
        generate(7), // nibble4
        generate(4), // nibble5
        generate(1), // nibble6
        generate(6), // nibble7
        generate(3)  // nibble8
      };

    return calculate(nibbles);
  }

  private int[] calculateNew() {
    int[] result = null;
    int min = Integer.MAX_VALUE;

    int[][] nibbles = new int[][]
      {
        generate(0), // nibble1
        generate(5), // nibble2
        generate(2), // nibble3
        generate(7), // nibble4
        generate(4), // nibble5
        generate(1), // nibble6
        generate(6), // nibble7
        generate(3)  // nibble8
      };

    int[][] permutate = new int[8][];
    for (int i = 0; i < (1 << 3) * 8; i++) {
      Arrays.fill(permutate, null);
      for (int j = 0, permutation = i; j < nibbles.length; j++) {
        int index = permutation & 0x7;
        permutation >>= 3;
        if (permutate[index] != null) {
          break;
        }
        permutate[index] = nibbles[j];
      }

      int[] merged = calculate(permutate);
      if (merged != null) {
        int length = length(merged);
        if (length < min) {
          min = length;
          result = merged;
        }
      }
    }

    return result;
  }

  /**
   * Generate a reverse gcr table.
   *
   * @param shift the number of bits the gcr is shifted
   * @return reverse gcr table
   */
  private int[] generate(int shift) {
    int[] nibble = new int[256];
    Arrays.fill(nibble, -1);
    for (int i = 0; i < GCR.length; i++) {
      int gcr = GCR[i];
      if (gcr >= 0) {
        int shifted = gcr << shift;
        nibble[(shifted & 0xFF) | (shifted >> 8)] = i;
      }
    }

    return nibble;
  }

  /**
   * Merge all reverse gcr tables.
   *
   * @param nibbles reverse gcr tables
   * @return the merged reverse gcr table
   */
  public int[] calculate(int[][] nibbles) {
    int[] result = new int[512];
    Arrays.fill(result, -1);

    for (int[] nibble : nibbles) {
      int i = 0; // -first(nibble);
      for (; i < 256; i++) {
        if (match(nibble, result, i)) {
          copy(nibble, result, i);
          break;
        }
      }

      if (i == 256) {
        return null;
      }
    }

    return result;
  }

  /**
   * Does the given reversed gcr table into the merged reversed gcr table.
   *
   * @param nibble reversed gcr table
   * @param result merged reversed gcr table
   * @param index the index to which nibble should be merged into result
   */
  private boolean match(int[] nibble, int[] result, int index) {
    for (int i = 0, pos = index; i < nibble.length; i++, pos++) {
      if (pos < 0) {
        continue;
      }
      if (nibble[i] >= 0 && result[i + index] >= 0) {
        return false;
      }
    }

    return true;
  }

  /**
   * Copy the given reversed gcr table into the merged reversed gcr table.
   *
   * @param nibble reversed gcr table
   * @param result merged reversed gcr table
   * @param index the index to which nibble should be merged into result
   */
  private void copy(int[] nibble, int[] result, int index) {
    for (int i = 0, pos = index; i < nibble.length; i++, pos++) {
      if (pos < 0) {
        continue;
      }
      if (nibble[i] >= 0) {
        result[i + index] = nibble[i];
      }
    }
  }

  /**
   * Length of the given gcr table.
   * Considers the first set byte to the last set byte.
   *
   * @param table gcr table
   */
  private static int length(int[] table) {
    int first = first(table);

    int last = table.length - 1;
    for (; last >= 0; last--) {
      if (table[last] >= 0) {
        break;
      }
    }

    return last - first + 1;
  }

  private static int first(int[] table) {
    int first = 0;
    for (; first < table.length; first++) {
      if (table[first] >= 0) {
        break;
      }
    }

    return first;
  }

  /**
   * Converts a gcr table to an easy to read string representation.
   *
   * @param table gcr table
   */
  private static String toString(int[] table) {
    StringBuilder out = new StringBuilder(256 * 5);
    for (int i = 0; i < table.length; ) {
      out.append(HexUtil.hexWord(i));
      out.append(": ");
      for (int j = 0; j < 16; i++, j++) {
        out.append(table[i] >= 0 ? HexUtil.hexByte(table[i]) : "---");
        out.append(", ");
      }
      out.append("\n");
    }

    return out.toString();
  }
}

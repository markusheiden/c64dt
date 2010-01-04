package de.heiden.c64dt.simulator;

/**
 * A short class to be simulated.
 */
public class Example {
  public void excute() {
    int a = 1;
    clock();
    for (int i = 0; i < 4; i++) {
      a = inc(a);
    }
    int b = 2 * a;
    System.out.println(b);
  }

  protected int inc(int a) {
    clock();
    return a++;
  }

  protected void clock() {
    // clock tick
  }
}

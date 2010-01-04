package de.heiden.c64dt.simulator;

/**
 * Modified version of class for simulation.
 */
public class ExampleSimulation {
  public void excute() {
    int a;
    {
      a = 1;
    }
    {
      clock();
    }
    {
      int i;
      for (i = 0; i < 4; i++) {
        {
          a = inc(a);
        }
      }
    }
    int b;
    {
      b = 2 * a;
    }
    System.out.println(b);
  }

  protected int inc(int a) {
    {
      clock();
    }
    {
      a++;
    }
    return a;
  }

  protected void clock() {
    // clock tick
  }
}

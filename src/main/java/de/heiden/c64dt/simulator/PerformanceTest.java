package main.java.de.heiden.c64dt.simulator;

import java.util.concurrent.CyclicBarrier;

/**
 * Test threading performance.
 */
public class PerformanceTest {
  public static long DURATION = 60 * 1000;

  private long i1;
  private long i2;
  private volatile boolean run;
  private volatile long start;
  private volatile long end;

  public static void main(String[] args) throws Exception {
//    new PerformanceTest().start();
//    new PerformanceTest().startThread();
    new PerformanceTest().startBarrier();
  }

  public void start() throws Exception {
    final Object lock = new Object();
    Thread thread = new Thread(new Runnable() {
      public void run() {
        try {
          synchronized (lock) {
            lock.wait();
          }
          start = System.currentTimeMillis();
          while(run) {
            for (int j = 0; j < 100; j++) {}
            inc1();
          }
        } catch (InterruptedException e) {
        }
        end = System.currentTimeMillis();
      }
    });

    thread.start();
    Thread.sleep(2000);

    run = true;
    synchronized (lock) {
      lock.notify();
    }

    Thread.sleep(DURATION);
    run = false;
    thread.join();

    long duration = end - start;
    long incs = i1 * 1000 / duration;
    System.out.println("inc/s = " + incs + " (" + duration + " ms)");
  }

  public void startThread() throws Exception {
    final Object lock = new Object();
    Thread thread1 = new Thread(new Runnable() {
      public void run() {
        try {
          synchronized (lock) {
            lock.wait();
            start = System.currentTimeMillis();
            while(true) {
              inc1();
              lock.notify();
              lock.wait();
            }
          }
        } catch (InterruptedException e) {
        }
        end = System.currentTimeMillis();
      }
    });
    Thread thread2 = new Thread(new Runnable() {
      public void run() {
        try {
          while(true) {
            synchronized (lock) {
              while(true) {
                lock.wait();
                inc1();
                lock.notify();
              }
            }
          }
        } catch (InterruptedException e) {
        }
      }
    });

    thread1.start();
    Thread.sleep(2000);
    thread2.start();
    Thread.sleep(2000);

    synchronized (lock) {
      lock.notify();
    }

    Thread.sleep(DURATION);
    thread1.interrupt();
    thread2.interrupt();
    thread1.join();
    thread2.join();

    long duration = end - start;
    long incs = i1 * 1000 / duration;
    System.out.println("inc/s = " + incs + " (" + duration + " ms)");
  }

  public void startBarrier() throws Exception {
    final CyclicBarrier barrier = new CyclicBarrier(2);
    final Object lock1 = new Object();
    final Object lock2 = new Object();
    Thread thread1 = new Thread(new Runnable() {
      public void run() {
        try {
          synchronized (lock1) {
            lock1.wait();
          }
          start = System.currentTimeMillis();
          while(true) {
            inc1();
            barrier.await();
          }
        } catch (Exception e) {
        }
        end = System.currentTimeMillis();
      }
    });
    Thread thread2 = new Thread(new Runnable() {
      public void run() {
        try {
          synchronized (lock2) {
            lock2.wait();
          }
          while(true) {
            inc2();
            barrier.await();
          }
        } catch (Exception e) {
        }
      }
    });

    thread1.start();
    thread2.start();
    Thread.sleep(2000);

    synchronized (lock1) {
      synchronized (lock2) {
        lock1.notifyAll();
        lock2.notifyAll();
      }
    }

    Thread.sleep(DURATION);
    thread1.interrupt();
    thread2.interrupt();
    thread1.join();
    thread2.join();

    long duration = end - start;
    long incs = (i1 + i2) * 1000 / duration;
    System.out.println("inc/s = " + incs + " (" + duration + " ms)");
  }

  public void inc1() {
    i1++;
  }

  public void inc2() {
    i2++;
  }
}

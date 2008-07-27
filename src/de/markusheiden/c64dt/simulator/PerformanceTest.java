package de.markusheiden.c64dt.simulator;

/**
 * Test threading performance.
 */
public class PerformanceTest {
  private int i;
  private volatile boolean run;

  public static void main(String[] args) throws Exception {
    new PerformanceTest().start();
    new PerformanceTest().startThread();
  }

  public void start() throws Exception {
    final Object lock = new Object();
    Thread thread = new Thread(new Runnable() {
      public void run() {
        try {
          synchronized (lock) {
            lock.wait();
          }
          while(run) {
            inc();
          }
        } catch (InterruptedException e) {
        }
      }
    });

    thread.start();
    Thread.sleep(1000);

    run = true;
    synchronized (lock) {
      lock.notify();
    }

    Thread.sleep(1000);
    run = false;
    thread.join();

    System.out.println("i = " + i);
  }

  protected void inc() {
    i++;
  }

  public void startThread() throws Exception {
    final Object lock = new Object();
    Thread thread1 = new Thread(new Runnable() {
      public void run() {
        try {
          synchronized (lock) {
            while(true) {
              lock.wait();
              inc();
              lock.notify();
            }
          }
        } catch (InterruptedException e) {
        }
      }
    });
    Thread thread2 = new Thread(new Runnable() {
      public void run() {
        try {
          while(true) {
            synchronized (lock) {
              while(true) {
                lock.wait();
                inc();
                lock.notify();
              }
            }
          }
        } catch (InterruptedException e) {
        }
      }
    });

    thread1.start();
    thread2.start();
    Thread.sleep(1000);

    synchronized (lock) {
      lock.notify();
    }

    Thread.sleep(10000);
    thread1.interrupt();
    thread2.interrupt();
    thread1.join();
    thread2.join();

    System.out.println("i = " + i);
  }
}

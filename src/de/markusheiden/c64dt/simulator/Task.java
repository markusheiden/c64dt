package de.markusheiden.c64dt.simulator;

/**
 * Task to execute in emulator.
 */
public interface Task {
  /**
   * Chain task.
   *
   * @param nextTask next task to be executed after this task
   */
  public void setNextTask(Task nextTask);

  /**
   * Execute this task.
   *
   * @return next task to execute
   */
  public Task execute();
}

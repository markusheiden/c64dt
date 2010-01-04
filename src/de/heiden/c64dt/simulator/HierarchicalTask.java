package de.heiden.c64dt.simulator;

/**
 * Task with subtasks to execute in emulator.
 */
public interface HierarchicalTask extends Task {
  /**
   * Add a sub task.
   *
   * @param subtask sub task
   */
  public void addSubtask(Task subtask);
}

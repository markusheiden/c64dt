package de.markusheiden.c64dt.simulator;

import org.springframework.util.Assert;

/**
 * Leaf task which has no subtasks.
 */
public class LeafTask implements Task {
  private String name;
  private Task parentTask;
  private Task nextTask;

  public LeafTask(HierarchicalTask parentTask, String name) {
    Assert.notNull(parentTask);
    Assert.notNull(name);

    this.parentTask = parentTask;
    this.name = name;

    parentTask.addSubtask(this);
  }

  public void setNextTask(Task nextTask) {
    this.nextTask = nextTask;
  }

  public final Task execute() {
    doExecute();
    return nextTask;
  }

  public void doExecute() {
    // do some stuff
    System.out.println(name);
  }
}

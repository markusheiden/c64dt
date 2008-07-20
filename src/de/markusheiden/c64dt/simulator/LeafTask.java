package de.markusheiden.c64dt.simulator;

import org.springframework.util.Assert;

/**
 * Leaf task which has no subtasks.
 */
public class LeafTask implements Task {
  private String name;
  private Task nextTask;

  public LeafTask(HierarchicalTask parentTask, String name) {
    this(name);
    Assert.notNull(parentTask);

    parentTask.addSubtask(this);
  }

  public LeafTask(String name) {
    Assert.notNull(name);

    this.name = name;
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

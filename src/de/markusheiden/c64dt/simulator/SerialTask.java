package de.markusheiden.c64dt.simulator;

import org.springframework.util.Assert;

/**
 * Implementation of a task.
 */
public class SerialTask implements HierarchicalTask {
  private String name;
  private Task firstTask;
  private Task lastTask;

  public SerialTask(HierarchicalTask parentTask, String name) {
    this(name);
    Assert.notNull(parentTask);

    parentTask.addSubtask(this);
  }

  public SerialTask(String name) {
    Assert.notNull(name);

    this.name = name;
  }

  public void addSubtask(Task subtask) {
    Assert.notNull(subtask);

    if (firstTask == null) {
      firstTask = subtask;
    } else {
      lastTask.setNextTask(subtask);
    }
    lastTask = subtask;
  }

  public void setNextTask(Task nextTask) {
    Assert.notNull(nextTask);

    lastTask.setNextTask(nextTask);
  }

  public final Task execute() {
    doExecute();
    return firstTask;
  }

  public void doExecute() {
    // do some stuff
    System.out.println(name);
  }

  public static void main(String[] args) {
    HierarchicalTask main = new SerialTask("main");
    HierarchicalTask sub1 = new SerialTask(main, "sub1");
    HierarchicalTask sub11 = new SerialTask(sub1, "sub11");
    Task sub111 = new LeafTask(sub11, "sub111");
    Task sub112 = new LeafTask(sub11, "sub112");
    Task sub12 = new LeafTask(sub1, "sub12");
    HierarchicalTask sub2 = new SerialTask(main, "sub2");
    HierarchicalTask sub21 = new SerialTask(sub2, "sub21");
    Task sub211 = new LeafTask(sub21, "sub211");
    Task sub212 = new LeafTask(sub21, "sub212");
    HierarchicalTask sub22 = new SerialTask(sub2, "sub22");

    Task current = main;
    while (current != null) {
      System.out.println("emulate clock");
      current = current.execute();
    }
  }
}

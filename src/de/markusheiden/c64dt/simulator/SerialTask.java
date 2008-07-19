package de.markusheiden.c64dt.simulator;

import org.springframework.util.Assert;

/**
 * Implementation of a task.
 */
public class SerialTask implements Task {
  private String name;
  private Task firstTask;
  private Task lastTask;

  public SerialTask(String name) {
    this.name = name;
  }

  public void setSubtasks(Task... subtasks) {
    Assert.notNull(subtasks);

    int i = 0;
    firstTask = subtasks[i];
    for (; i < subtasks.length - 1; i++) {
      subtasks[i].setNextTask(subtasks[i + 1]);
    }
    lastTask = subtasks[i];
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
    SerialTask main = new SerialTask("main");
    SerialTask sub1 = new SerialTask("sub1");
    Task sub11 = new LeafTask("sub11");
    Task sub12 = new LeafTask("sub12");
    sub1.setSubtasks(sub11, sub12);
    SerialTask sub2 = new SerialTask("sub2");
    Task sub21 = new LeafTask("sub21");
    sub2.setSubtasks(sub21);
    Task end = new LeafTask("end");
    main.setSubtasks(sub1, sub2, end);

    Task current = main;
    while (current != null) {
      System.out.println("emulate clock");
      current = current.execute();
    }
  }
}

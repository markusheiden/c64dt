package de.heiden.c64dt.assembler.gui.event;

import org.springframework.context.ApplicationEvent;

/**
 * Superclass for all reassembler events.
 */
public abstract class ReassemblerEvent extends ApplicationEvent
{
  /**
   * Constructor.
   *
   * @param source Source of event
   */
  public ReassemblerEvent(Object source)
  {
    super(source);
  }
}

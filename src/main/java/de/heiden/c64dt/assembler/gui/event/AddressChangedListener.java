package de.heiden.c64dt.assembler.gui.event;

/**
 * Listener for {@link AddressChangedEvent}.
 */
public interface AddressChangedListener
{
  /**
   * The current address has been changed.
   *
   * @param event Event
   */
  public void addressChanged(AddressChangedEvent event);
}

package de.heiden.c64dt.assembler.gui.event;

/**
 * The select address has changed event.
 */
public class AddressChangedEvent extends ReassemblerEvent {
  /**
   * The current relative address.
   */
  private final int index;

  /**
   * Constructor for the case that no address has been selected.
   *
   * @param eventSource Source of event
   */
  public AddressChangedEvent(Object eventSource) {
    this(eventSource, -1);
  }

  /**
   * Constructor for the case that an address has been selected.
   *
   * @param eventSource Source of event
   * @param index The current relative address or -1
   */
  public AddressChangedEvent(Object eventSource, int index) {
    super(eventSource);

    this.index = index;
  }

  /**
   * Currently selected relative address.
   *
   * @return Relative address or -1, if no address has been selected
   */
  public int getIndex() {
    return index;
  }
}

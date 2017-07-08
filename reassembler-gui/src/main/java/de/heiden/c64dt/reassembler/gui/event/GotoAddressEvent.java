package de.heiden.c64dt.reassembler.gui.event;

/**
 * Go to the given address event.
 */
public class GotoAddressEvent extends ReassemblerEvent {
  /**
   * The relative address to go to.
   */
  private final int index;

  /**
   * Constructor for the case that no address has been selected.
   *
   * @param eventSource Source of event
   */
  public GotoAddressEvent(Object eventSource) {
    this(eventSource, -1);
  }

  /**
   * Constructor for the case that an address has been selected.
   *
   * @param eventSource Source of event
   * @param index The relative address to go to
   */
  public GotoAddressEvent(Object eventSource, int index) {
    super(eventSource);

    this.index = index;
  }

  /**
   * The relative address to go to.
   *
   * @return Relative address
   */
  public int getIndex() {
    return index;
  }
}

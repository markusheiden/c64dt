package de.heiden.c64dt.reassembler.gui;

import de.heiden.c64dt.reassembler.Reassembler;
import de.heiden.c64dt.reassembler.command.CommandBuffer;

import javax.swing.table.DefaultTableModel;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

import static de.heiden.c64dt.bytes.HexUtil.hexWordPlain;

/**
 * Table model for cross reference view.
 */
public class CrossReferenceTableModel extends DefaultTableModel {
  /**
   * Underlying representation: the reassembler.
   */
  private Reassembler reassembler;

  /**
   * Mapping from row to relative address of the code shown in that row.
   */
  private final Map<Integer, Integer> rowToIndex = new HashMap<>();
  private final Map<Integer, Integer> indexToRow = new HashMap<>();

  /**
   * Relative address for which the references should be displayed.
   */
  private int index;

  /**
   * Constructor.
   */
  public CrossReferenceTableModel() {
    super(new String[]{"Index", "Addr"}, 0);
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    return false;
  }

  /**
   * Get underlying reassembler.
   */
  public Reassembler getReassembler() {
    return reassembler;
  }

  /**
   * Use another reassembler.
   *
   * @param reassembler Underlying representation
   */
  public void use(Reassembler reassembler) {
    this.reassembler = reassembler;
    update();
  }

  /**
   * Select a relative address to show the cross reference for.
   *
   * @param index Relative address
   */
  public void select(int index) {
    this.index = index;
    update();
  }

  /**
   * Update table model from commands.
   */
  public void update() {
    setRowCount(0);
    rowToIndex.clear();
    indexToRow.clear();

    // no model -> no representation
    if (reassembler == null) {
      return;
    }

    CommandBuffer commands = reassembler.getCommands();

    if (!commands.hasIndex(index)) {
      // no row selected -> display nothing
      return;
    }

    SortedSet<Integer> references = commands.getReferences(commands.addressForIndex(index));
    for (Integer reference : references) {
      addRow(reference, commands.addressForIndex(reference));
    }
  }

  /**
   * Add a row to the model.
   *
   * @param index Relative address
   * @param address Absolute address
   */
  private void addRow(int index, int address) {
    rowToIndex.put(getRowCount(), index);
    indexToRow.put(index, getRowCount());
    addRow(new Object[]{hexWordPlain(index), hexWordPlain(address)});
  }

  /**
   * The relative address of the code shown in a given row.
   *
   * @param row Row
   */
  public Integer getIndex(int row) {
    return rowToIndex.get(row);
  }

  /**
   * The row in which the relative address is shown.
   *
   * @param index Index
   */
  public Integer getRow(int index) {
    return indexToRow.get(index);
  }
}

package de.heiden.c64dt.assembler.gui;

import de.heiden.c64dt.assembler.Reassembler;
import de.heiden.c64dt.assembler.gui.event.AddressChangedEvent;
import de.heiden.c64dt.assembler.gui.event.GotoAddressEvent;
import de.heiden.c64dt.assembler.gui.event.ReassemblerEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * View for cross reference of current opcode.
 */
@Component
public class CrossReferenceView implements ApplicationListener<ReassemblerEvent> {
  /**
   * Model.
   */
  private CrossReferenceTableModel model;

  /**
   * Spring event publisher.
   */
  @Autowired
  private ApplicationEventPublisher publisher;

  /**
   * Constructor.
   */
  public CrossReferenceView() {
    this.model = new CrossReferenceTableModel();
  }

  /**
   * Use another reassembler.
   *
   * @param reassembler Reassembler
   */
  public void use(Reassembler reassembler) {
    Assert.notNull(reassembler, "Precondition: reassembler != null");

    model.use(reassembler);
  }

  /**
   * Create GUI representation.
   */
  public JComponent createComponent() {
    final JTable table = new JTable(model);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    TableColumnModel columnModel = table.getColumnModel();
    columnModel.getColumn(0).setMaxWidth(40);
    columnModel.getColumn(0).setCellRenderer(new GreyRenderer());
    columnModel.getColumn(1).setMaxWidth(40);
    table.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setPreferredScrollableViewportSize(new Dimension(80, 0));

    table.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          publisher.publishEvent(new GotoAddressEvent(this, model.getIndex(table.rowAtPoint(e.getPoint()))));
        }
      }
    });

    JScrollPane scroll = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scroll.setBorder(BorderFactory.createTitledBorder("References"));

    return scroll;
  }

  @Override
  public void onApplicationEvent(ReassemblerEvent event) {
    if (event instanceof AddressChangedEvent) {
      select(((AddressChangedEvent) event).getIndex());
    }
  }

  /**
   * Select a relative address to show the cross reference for.
   *
   * @param index Relative address
   */
  public void select(int index) {
    model.select(index);
  }
}

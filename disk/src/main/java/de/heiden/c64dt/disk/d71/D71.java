package de.heiden.c64dt.disk.d71;

import de.heiden.c64dt.disk.AbstractDiskImage;
import de.heiden.c64dt.disk.BAM;
import de.heiden.c64dt.disk.IBAM;

import static de.heiden.c64dt.disk.SectorModelUtil.requireValidTrack;

/**
 * D71 (1571) disk image implementation.
 */
public class D71 extends AbstractDiskImage {
  /**
   * Constructor.
   *
   * @param hasErrors Support error information?
   */
  public D71(int tracks, boolean hasErrors) {
    super(2, tracks, hasErrors);
  }

  @Override
  public int getSectors() {
    return 21;
  }

  @Override
  public int getSectors(int track) {
    requireValidTrack(this, track);

    // Both sides always share the same sector model.
    var tps = getTracksPerSide();
    if (track > tps) {
      track -= tps;
    }

    if (track < 18) {
      return 21;
    } else if (track < 25) {
      return 19;
    } else if (track < 31) {
      return 18;
    } else {
      return 17;
    }
  }

  @Override
  public IBAM getBAM() {
    var result = new BAM(this);

    var bam = getSector(18, 0);
    var bam2 = getSector(53, 0);
    var tps = getTracksPerSide();
    for (int track = 1, pos = 0x04; track <= tps; track++, pos += 4) {
      readBAM(result, track, bam, pos);
      readBAM(result, track + tps, bam2, pos);
      // TODO Implement extra free sector count information from $DD-$FF?
    }

    return result;
  }

  @Override
  protected int getBamEntrySize() {
    return 4;
  }
}

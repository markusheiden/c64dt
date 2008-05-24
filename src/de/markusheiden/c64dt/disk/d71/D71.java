package de.markusheiden.c64dt.disk.d71;

import de.markusheiden.c64dt.disk.AbstractDiskImage;
import de.markusheiden.c64dt.disk.BAM;
import de.markusheiden.c64dt.disk.IBAM;
import static de.markusheiden.c64dt.disk.SectorModelUtil.assertTrack;
import org.springframework.util.Assert;

/**
 * D71 (1571) disk image implementation.
 */
public class D71 extends AbstractDiskImage {
  /**
   * Constructor.
   *
   * @param hasErrors support error informations?
   */
  public D71(int tracks, boolean hasErrors) {
    super(2, tracks, hasErrors);
  }

  public int getSectors() {
    return 21;
  }

  public int getSectors(int track) {
    assertTrack(this, track);

    // Both sides always share the same sector model
    int tps = getTracksPerSide();
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
    BAM result = new BAM(this);

    byte[] bam = getSector(18, 0);
    byte[] bam2 = getSector(53, 0);
    int tps = getTracksPerSide();
    for (int track = 1, pos = 0x04; track <= tps; track++, pos += 4) {
      readBAM(result, track, bam, pos);
      readBAM(result, track + tps, bam2, pos);
      // TODO implement extra free sector count informations from $DD-$FF?
    }

    Assert.notNull(result, "Postcondition: result != null");
    return result;
  }

  protected int getBamEntrySize() {
    return 4;
  }
}

package de.heiden.c64dt.disk;

import org.springframework.util.Assert;
import static de.heiden.c64dt.disk.SectorModelUtil.assertTrack;
import static de.heiden.c64dt.disk.SectorModelUtil.assertSector;

/**
 * Block allocation map implementation.
 */
public class BAM implements IBAM {
  private ISectorModel sectorModel;
  private boolean[][] isFree;
  private int[] freeSectors;

  public BAM(ISectorModel sectorModel) {
    Assert.notNull(sectorModel, "Precondition: sectorModel != null");

    this.sectorModel = sectorModel;

    int tracks = sectorModel.getTracks();
    isFree = new boolean[tracks][];
    freeSectors = new int[tracks];
    for (int track = 1; track <= tracks; track++) {
      isFree[track - 1] = new boolean[sectorModel.getSectors(track)];
      freeSectors[track - 1] = 0;
    }
  }

  //
  // IBAM
  //

  public boolean isFree(int track, int sector) {
    assertSector(sectorModel, track, sector);

    return isFree[track - 1][sector];
  }

  public void setFree(int track, int sector, boolean isFree) {
    assertSector(sectorModel, track, sector);

    this.isFree[track - 1][sector] = isFree;
  }

  public int getFreeSectors() {
    int free = 0;
    for (int track = 1; track < sectorModel.getTracks(); track++) {
      free += getFreeSectors(track);
    }

    return free;
  }

  public int getFreeSectors(int track) {
    assertTrack(sectorModel, track);

    return freeSectors[track - 1];
  }

  public void setFreeSectors(int track, int freeSectors) {
    assertTrack(sectorModel, track);

    this.freeSectors[track - 1] = freeSectors;
  }
}
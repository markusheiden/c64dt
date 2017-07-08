package de.heiden.c64dt.disk;

import org.springframework.util.Assert;

import static de.heiden.c64dt.disk.SectorModelUtil.assertSector;
import static de.heiden.c64dt.disk.SectorModelUtil.assertTrack;

/**
 * Block allocation map implementation.
 */
public class BAM implements IBAM {
  private final ISectorModel sectorModel;
  private final boolean[][] isFree;
  private final int[] freeSectors;

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

  @Override
  public boolean isFree(int track, int sector) {
    assertSector(sectorModel, track, sector);

    return isFree[track - 1][sector];
  }

  @Override
  public void setFree(int track, int sector, boolean isFree) {
    assertSector(sectorModel, track, sector);

    this.isFree[track - 1][sector] = isFree;
  }

  @Override
  public int getFreeSectors() {
    int free = 0;
    for (int track = 1; track < sectorModel.getTracks(); track++) {
      free += getFreeSectors(track);
    }

    return free;
  }

  @Override
  public int getFreeSectors(int track) {
    assertTrack(sectorModel, track);

    return freeSectors[track - 1];
  }

  @Override
  public void setFreeSectors(int track, int freeSectors) {
    assertTrack(sectorModel, track);

    this.freeSectors[track - 1] = freeSectors;
  }
}

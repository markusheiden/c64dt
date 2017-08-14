package de.heiden.c64dt.disk;

import static de.heiden.c64dt.disk.SectorModelUtil.requireValidSector;
import static de.heiden.c64dt.disk.SectorModelUtil.requireValidTrack;
import static org.bitbucket.cowwoc.requirements.core.Requirements.requireThat;

/**
 * Block allocation map implementation.
 */
public class BAM implements IBAM {
  private final ISectorModel sectorModel;
  private final boolean[][] isFree;
  private final int[] freeSectors;

  public BAM(ISectorModel sectorModel) {
    requireThat("sectorModel", sectorModel).isNotNull();

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
    requireValidSector(sectorModel, track, sector);

    return isFree[track - 1][sector];
  }

  @Override
  public void setFree(int track, int sector, boolean isFree) {
    requireValidSector(sectorModel, track, sector);

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
    requireValidTrack(sectorModel, track);

    return freeSectors[track - 1];
  }

  @Override
  public void setFreeSectors(int track, int freeSectors) {
    requireValidTrack(sectorModel, track);

    this.freeSectors[track - 1] = freeSectors;
  }
}

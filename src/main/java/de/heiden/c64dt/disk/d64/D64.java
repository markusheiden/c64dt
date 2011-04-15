package de.heiden.c64dt.disk.d64;

import de.heiden.c64dt.disk.AbstractDiskImage;
import de.heiden.c64dt.disk.BAM;
import de.heiden.c64dt.disk.IBAM;
import org.springframework.util.Assert;

import static de.heiden.c64dt.disk.SectorModelUtil.assertTrack;

/**
 * D64 (1541) disk image implementation.
 */
public class D64 extends AbstractDiskImage
{
  /**
   * Constructor.
   *
   * @param tracks number of tracks
   * @param hasErrors support error informations?
   */
  public D64(int tracks, boolean hasErrors)
  {
    super(1, tracks, hasErrors);
  }

  //
  // ISectorModel
  //

  public int getSectors()
  {
    return 21;
  }

  public int getSectors(int track)
  {
    assertTrack(this, track);

    if (track < 18)
    {
      return 21;
    }
    else if (track < 25)
    {
      return 19;
    }
    else if (track < 31)
    {
      return 18;
    }
    else
    {
      return 17;
    }
  }

  //
  // BAM, directory
  //

  public IBAM getBAM()
  {
    BAM result = new BAM(this);

    byte[] bam = getSector(18, 0);
    int tracks = getTracks();
    for (int track = 1, pos = 0x04; track <= tracks; track++, pos += 4)
    {
      if (track == 37)
      {
        // TODO currently only Dolphin DOS support implemented
        pos = 0xAC;
      }
      readBAM(result, track, bam, pos);
    }

    Assert.notNull(result, "Postcondition: result != null");
    return result;
  }

  protected int getBamEntrySize()
  {
    return 4;
  }
}

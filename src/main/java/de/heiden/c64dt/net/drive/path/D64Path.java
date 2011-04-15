package de.heiden.c64dt.net.drive.path;

import de.heiden.c64dt.disk.IDirectory;
import de.heiden.c64dt.disk.IDiskImage;
import de.heiden.c64dt.disk.WrongDiskImageFormatException;
import de.heiden.c64dt.disk.d64.D64Reader;
import de.heiden.c64dt.net.drive.stream.IStream;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Path to a d64 image file.
 */
public class D64Path extends AbstractPath
{
  private IDiskImage d64;

  public D64Path(IPath parent, File d64) throws FileNotFoundException
  {
    super(parent);

    Assert.notNull(d64, "Precondition: d64 != null");

    try
    {
      this.d64 = new D64Reader().read(d64);
    }
    catch (IOException e)
    {
      throw new FileNotFoundException(d64.getPath() + " could not be read");
    }
    catch (WrongDiskImageFormatException e)
    {
      throw new FileNotFoundException(d64.getPath() + " is an invalid d64 image");
    }
  }

  protected IDirectory doDirectory()
  {
    return d64.getDirectory();
  }

  protected IStream doFile(byte[] filename) throws FileNotFoundException
  {
    // TODO implement
    return null;
  }

  protected IPath doChangePath(byte[] path) throws FileNotFoundException
  {
    throw new FileNotFoundException("D64 images do not support directories");
  }
}

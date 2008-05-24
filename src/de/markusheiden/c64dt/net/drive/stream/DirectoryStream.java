package de.markusheiden.c64dt.net.drive.stream;

import de.markusheiden.c64dt.disk.IDirectory;
import de.markusheiden.c64dt.disk.IFile;
import de.markusheiden.c64dt.util.ByteUtil;
import org.springframework.util.Assert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Creates stream from a directory.
 */
public class DirectoryStream extends AbstractStream {
  private static final byte DOUBLE_QUOTE = encode("\"")[0];
  private static final byte SPACE = encode(" ")[0];
  private static final byte LOWER = encode("<")[0];
  private static final byte[] BLOCKS_FREE = encode(" BLOCKS FREE");

  private ByteArrayOutputStream baos;
  private byte[] content;

  public DirectoryStream(IDirectory directory) {
    Assert.notNull(directory, "Precondition: directory != null");

    baos = new ByteArrayOutputStream(4096);
    writeWord(0x0401);

    writeHeader(directory);
    for (IFile file : directory.getFiles()) {
      if (file.getMode().isVisible()) {
        writeFile(file);
      }
    }
    writeFooter(directory);

    writeByte(0x00);

    content = baos.toByteArray();
    baos = null;
  }

  private void writeHeader(IDirectory directory) {
    writeWord(0x0101);
    writeWord(0x0000);
    writeByte(0x12);
    writeByte(DOUBLE_QUOTE);
    writeBytes(directory.getName());
    writeByte(DOUBLE_QUOTE);
    writeByte(SPACE);
    writeBytes(directory.getIdAndType());
    writeByte(0x00);
  }

  private void writeFile(IFile file) {
    String size = (Integer.toString(file.getSize()) + "    ").substring(0, 4);
    String type = file.getMode().getType().toString();

    writeWord(0x0101);
    writeBytes(encode(size));
    writeByte(DOUBLE_QUOTE);
    writeBytes(file.getName());
    writeByte(DOUBLE_QUOTE);
    for (int i = 0; i < 16 - file.getName().length; i++) {
      writeByte(SPACE);
    }
    writeByte(SPACE);
    writeBytes(encode(type));
    writeByte(file.getMode().isLocked()? LOWER : SPACE);
    writeByte(0x00);
  }

  private void writeFooter(IDirectory directory) {
    // TODO implement
    String free = (Integer.toString(directory.getFreeBlocks()) + "    ").substring(0, 4);

    writeWord(0x0101);
    writeBytes(encode(free));
    writeBytes(BLOCKS_FREE);
    writeByte(0x00);
  }

  private void writeByte(int b) {
    baos.write(ByteUtil.toByte(b));
  }

  private void writeBytes(byte... b) {
    baos.write(b, 0, b.length);
  }

  private void writeWord(int w) {
    baos.write(ByteUtil.toByte(w & 0xFF));
    baos.write(ByteUtil.toByte(w >> 8));
  }

  public byte[] doRead(int length) {
    Assert.isTrue(length >= 0, "Precondition: length >= 0");

    byte[] result = new byte[limitLength(content.length, length)];
    System.arraycopy(content, getPosition(), result, 0, result.length);

    Assert.notNull(result, "Postcondition: result != null");
    return result;
  }

  public void doWrite(byte[] data) throws IOException {
    throw new IOException("Writing to a directory stream is not supported");
  }
}

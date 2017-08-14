package de.heiden.c64dt.net.drive.stream;

import de.heiden.c64dt.bytes.ByteUtil;
import de.heiden.c64dt.disk.IDirectory;
import de.heiden.c64dt.disk.IFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static de.heiden.c64dt.net.drive.DeviceEncoding.encode;
import static org.bitbucket.cowwoc.requirements.core.Requirements.requireThat;

/**
 * Creates stream from a directory.
 */
public class DirectoryStream extends AbstractStream {
  private static final byte DOUBLE_QUOTE = encode('"');
  private static final byte SPACE = encode(' ');
  private static final byte LOWER = encode('<');
  private static final byte[] BLOCKS_FREE = encode("BLOCKS FREE");

  private ByteArrayOutputStream baos;
  private byte[] content;

  public DirectoryStream(IDirectory directory) {
    requireThat("directory", directory).isNotNull();

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
    int size = Math.min(file.getSize(), 9999);

    writeWord(0x0101);
    writeWord(size);
    for (int i = 0; i < 4 - Integer.toString(size).length(); i++) {
      writeByte(SPACE);
    }
    writeByte(DOUBLE_QUOTE);
    writeBytes(file.getName());
    writeByte(DOUBLE_QUOTE);
    for (int i = 0; i < 16 - file.getName().length; i++) {
      writeByte(SPACE);
    }
    writeByte(SPACE);
    writeBytes(encode(file.getMode().getType().getExtension()));
    writeByte(file.getMode().isLocked() ? LOWER : SPACE);
    writeByte(SPACE);
    writeByte(0x00);
  }

  private void writeFooter(IDirectory directory) {
    writeWord(0x0101);
    writeWord(directory.getFreeBlocks());
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

  @Override
  public byte[] doRead(int length) {
    requireThat("length", length).isGreaterThanOrEqualTo(0);

    byte[] result = new byte[limitLength(content.length, length)];
    System.arraycopy(content, getPosition(), result, 0, result.length);

    requireThat("result", result).isNotNull();
    return result;
  }

  @Override
  public void doWrite(byte[] data) throws IOException {
    throw new IOException("Writing to a directory stream is not supported");
  }
}

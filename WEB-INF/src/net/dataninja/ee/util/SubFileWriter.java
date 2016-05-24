package net.dataninja.ee.util;


/**
net.dataninja copyright statement
 */
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Represents a single sub-file within a {@link StructuredFile}. A sub-file
 * provides standard DataInput/DataOutput facilities, and takes care of
 * writing to the correct subset of the main StructuredFile.
 *
 * @author Rick Li
 */
class SubFileWriter extends SubStoreWriter 
{
  /** Actual disk file to write to */
  private RandomAccessFile file;

  /** The structured file that owns this Subfile */
  private StructuredFile parent;

  /** Absolute file position for the subfile's start */
  private long segOffset;

  /** Current write position within the subfile */
  private long writtenPos = 0;

  /** Size of the buffer to maintain */
  private static final int BUF_SIZE = 32768;

  /** Buffered data (cuts down access to the physical file) */
  private byte[] buf = new byte[BUF_SIZE];

  /** Amount of data buffered */
  private int bufTop = 0;

  /**
   * Construct a subfile writer.
   *
   * @param file      Disk file to attach to
   * @param parent    Structured file to attach to
   * @param segOffset Beginning offset of the segment
   */
  SubFileWriter(RandomAccessFile file, StructuredFile parent, long segOffset)
    throws IOException 
  {
    this.file = file;
    this.parent = parent;
    this.segOffset = segOffset;
  }

  public void close()
    throws IOException 
  {
    synchronized (parent) 
    {
      // Force a flush.
      checkLength(BUF_SIZE);

      // And notify the main StructuredFile.
      parent.closeWriter(this);
      file = null;
    }
  }

  public long length()
    throws IOException 
  {
    return writtenPos + bufTop;
  }

  /**
   * Ensure that the buffer has room for the specified number of bytes.
   * If not, it is flushed.
   *
   * @param nBytes    Amount of space desired
   */
  private void checkLength(int nBytes)
    throws IOException 
  {
    if (parent.curSubFile != this) {
      file.seek(writtenPos + segOffset);
      parent.curSubFile = this;
    }

    if (bufTop + nBytes > BUF_SIZE) {
      file.write(buf, 0, bufTop);
      writtenPos += bufTop;
      bufTop = 0;
    }
  }

  public void write(byte[] b)
    throws IOException 
  {
    write(b, 0, b.length);
  }

  public void write(byte[] b, int off, int len)
    throws IOException 
  {
    synchronized (parent) 
    {
      checkLength(len);
      if (len > BUF_SIZE) {
        file.write(b, off, len);
        writtenPos += len;
      }
      else {
        System.arraycopy(b, off, buf, bufTop, len);
        bufTop += len;
      }
    }
  }

  public void writeByte(int v)
    throws IOException 
  {
    synchronized (parent) {
      checkLength(1);
      buf[bufTop++] = (byte)(v & 0xff);
    }
  }

  public void writeInt(int v)
    throws IOException 
  {
    synchronized (parent) {
      checkLength(4);
      buf[bufTop++] = (byte)((v >>> 24) & 0xFF);
      buf[bufTop++] = (byte)((v >>> 16) & 0xFF);
      buf[bufTop++] = (byte)((v >>> 8) & 0xFF);
      buf[bufTop++] = (byte)((v >>> 0) & 0xFF);
    }
  }
} // class Subfile

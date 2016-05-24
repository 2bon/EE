package net.dataninja.ee.util;


/**
net.dataninja copyright statement
 */
import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Reads a single sub-file within a {@link StructuredFile}. A sub-file
 * provides standard DataInput/DataOutput facilities, and takes care of
 * reading from the correct subset of the main StructuredFile.
 *
 * @author Rick Li
 */
class SubFileReader extends SubStoreReader 
{
  /** Actual disk file to write to */
  private RandomAccessFile file;

  /** The structured file that owns this Subfile */
  private StructuredFile parent;

  /** Absolute file position for the subfile's start */
  private long segOffset;

  /** Length of this subfile */
  private long segLength;

  /** Current read position within the subfile */
  private long curPos;

  /**
   * Construct a subfile reader. Reads will be constrained to the
   * specified limit.
   *
   * @param file      Disk file to attach to
   * @param parent    Structured file to attach to
   * @param segOffset Beginning offset of the segment
   * @param segLength Length of the segment
   */
  SubFileReader(RandomAccessFile file, StructuredFile parent, long segOffset,
                long segLength)
    throws IOException 
  {
    this.file = file;
    this.parent = parent;
    this.segOffset = segOffset;
    this.segLength = segLength;
    curPos = 0;
  }

  public void close()
    throws IOException 
  {
    synchronized (parent) {
      parent.closeReader(this);
      file = null;
    }
  }

  public long getFilePointer()
    throws IOException 
  {
    return curPos;
  }

  public long length()
    throws IOException 
  {
    return segLength;
  }

  /**
   * Ensure that the sub-file has room to read the specified number of
   * bytes. As a side-effect, we also check that the main file position
   * is current for this sub-file, and if not, we save the position for
   * the other sub-file and restore ours.
   *
   * @param nBytes    Amount of space desired
   */
  private void checkLength(int nBytes)
    throws IOException 
  {
    synchronized (parent) 
    {
      if (parent.curSubFile != this) {
        file.seek(segOffset + curPos);
        parent.curSubFile = this;
      }

      if (curPos + nBytes > segLength)
        throw new EOFException("End of sub-file reached");
    }
  }

  public void read(byte[] b, int off, int len)
    throws IOException 
  {
    synchronized (parent) {
      checkLength(len);
      file.readFully(b, off, len);
      curPos += len;
    }
  }

  public void seek(long pos)
    throws IOException 
  {
    synchronized (parent) {
      if (segLength >= 0 && pos > segLength)
        throw new EOFException("Cannot seek past end of subfile");
      parent.curSubFile = this;
      file.seek(pos + segOffset);
      curPos = pos;
    }
  }

  public byte readByte()
    throws IOException 
  {
    synchronized (parent) {
      checkLength(1);
      byte ret = file.readByte();
      curPos++;
      return ret;
    }
  }

  public int readInt()
    throws IOException 
  {
    synchronized (parent) {
      checkLength(4);
      int ret = file.readInt();
      curPos += 4;
      return ret;
    }
  }
} // class Subfile

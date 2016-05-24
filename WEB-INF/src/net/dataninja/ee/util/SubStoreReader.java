package net.dataninja.ee.util;


/*
net.dataninja copyright statement
 */

/*
 * This file created on Mar 11, 2005 by Rick Li
 */
import java.io.IOException;

/**
 * Reads from a single sub-store within a {@link StructuredStore}. A sub-store
 * provides most of the interface of a RandomAccessFile, and takes care of
 * reading from the correct subset of the main StructuredStore.
 *
 * @author Rick Li
 */
public abstract class SubStoreReader 
{
  public abstract void close()
    throws IOException;

  public abstract long getFilePointer()
    throws IOException;

  public abstract long length()
    throws IOException;

  public void read(byte[] b)
    throws IOException 
  {
    read(b, 0, b.length);
  }

  public abstract void read(byte[] b, int off, int len)
    throws IOException;

  public abstract void seek(long pos)
    throws IOException;

  public abstract byte readByte()
    throws IOException;

  public abstract int readInt()
    throws IOException;
}

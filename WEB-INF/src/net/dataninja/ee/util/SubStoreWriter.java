package net.dataninja.ee.util;

import java.io.IOException;

/*
net.dataninja copyright statement
 */

/*
 * This file created on Mar 11, 2005 by Rick Li
 */

/**
 * Writes to a single sub-store within a {@link StructuredStore}. A sub-store
 * provides most of the interface of a RandomAccessFile, and takes care of
 * writing to the correct subset of the main StructuredStore.
 *
 * @author Rick Li
 */
public abstract class SubStoreWriter 
{
  public void write(byte[] b)
    throws IOException 
  {
    write(b, 0, b.length);
  }

  public abstract void write(byte[] b, int off, int len)
    throws IOException;

  public abstract void writeByte(int b)
    throws IOException;

  public void writeChars(String s)
    throws IOException 
  {
    int clen = s.length();
    int blen = 2 * clen;
    byte[] b = new byte[blen];
    char[] c = new char[clen];
    s.getChars(0, clen, c, 0);
    for (int i = 0, j = 0; i < clen; i++) {
      b[j++] = (byte)(c[i] >>> 8);
      b[j++] = (byte)(c[i] >>> 0);
    }
    write(b);
  }

  public abstract void writeInt(int v)
    throws IOException;

  public abstract long length()
    throws IOException;

  public abstract void close()
    throws IOException;
}

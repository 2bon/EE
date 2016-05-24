package net.dataninja.ee.textIndexer;


/**
net.dataninja copyright statement
 */
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Enumeration;

/*
 * This file created on Jan 21, 2005 by Rick Li
 */

/**
 * There's a very nasty bug in the Apache Crimson XML parser. If a ']'
 * character appears at the very end of its 8193-byte buffer and is
 * preceded by a '>' then it crashes. This stream works around it by
 * inserting spaces just before ']' if preceded by a '>'.
 */
public class CrimsonBugWorkaround extends SequenceInputStream 
{
  InputStream in;

  /** Construct a stream that filters the given one */
  public CrimsonBugWorkaround(InputStream in) {
    super(new BlockEnum(in));
  } // constructor

  /** Presents the input stream as a series of blocks of data */
  private static class BlockEnum implements Enumeration 
  {
    static final int BLOCK_SIZE = 32 * 1024;
    InputStream in;
    byte[] inBuf = new byte[BLOCK_SIZE];
    int inBufLen;
    byte[] outBuf = new byte[BLOCK_SIZE * 2];
    int outBufLen;
    boolean eof = false;
    byte prev = 'a';

    BlockEnum(InputStream in) {
      this.in = in;
    }

    /** Tells whether there are more blocks to read */
    public boolean hasMoreElements() {
      return !eof;
    }

    /** Gets an InputStream for the next block of data */
    public Object nextElement() 
    {
      try 
      {
        inBufLen = 0;
        while (!eof && inBufLen < BLOCK_SIZE) 
        {
          int nRead = in.read(inBuf, inBufLen, BLOCK_SIZE - inBufLen);
          if (nRead < 0) {
            eof = true;
            break;
          }
          inBufLen += nRead;
          assert inBufLen <= BLOCK_SIZE;
        }
        fixBuf();
        return new ByteArrayInputStream(outBuf, 0, outBufLen);
      }
      catch (IOException e) {
        throw new RuntimeException(e);
      }
    } // nextElement()

    /**
     * Scan through the input buffer, looking for the suspicious pair of
     * characters and sticking a space between them. The result is in
     * the output buffer.
     */
    private void fixBuf() 
    {
      outBufLen = 0;
      for (int src = 0; src < inBufLen; src++) 
      {
        byte cur = inBuf[src];

        if (cur == ']' && prev == '>')
          outBuf[outBufLen++] = ' ';

        outBuf[outBufLen++] = cur;
        prev = cur;
      } // for src
    } // fixBuf()
  } // class BlockEnum
} // class CrimsonBugWorkaround

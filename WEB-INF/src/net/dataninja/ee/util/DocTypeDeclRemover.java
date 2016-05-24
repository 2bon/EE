package net.dataninja.ee.util;


/**
net.dataninja copyright statement
 */
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * The parser that comes with the JDK always tries to resolve DOCTYPE
 * declarations in an XML file, but will barf if it can't. We want to be
 * able to work with such documents regardless of whether the DOCTYPE
 * is resolvable or not. Hence this class, which filters out DOCTYPE
 * declarations entirely.
 */
public class DocTypeDeclRemover extends BufferedInputStream 
{
  /** Marks whether we've scanned the initial block for a DOCTYPE decl */
  private boolean firstTime = true;

  /** How many bytes to scan before giving up */
  private static final int BLOCK_SIZE = 16384;

  /** Default constructor: records the input stream to filter. */
  public DocTypeDeclRemover(InputStream in) {
    super(in, BLOCK_SIZE);
  }

  /**
   * See
   * the general contract of the <code>read</code>
   * method of <code>InputStream</code>.
   *
   * @return     the next byte of data, or <code>-1</code> if the end of the
   *             stream is reached.
   * @exception  IOException  if an I/O error occurs.
   * @see        java.io.FilterInputStream#in
   */
  public int read()
    throws IOException 
  {
    if (firstTime) {
      byte[] buf = new byte[1];
      if (read(buf, 0, 1) != 1)
        return -1;
      return buf[0] & 0xff;
    }
    else
      return super.read();
  }

  /**
   * Read a block of bytes. The first {@link #BLOCK_SIZE} bytes will be
   * scanned for a DOCTYPE declaration, and if one is found it will be
   * converted to an XML comment.
   *
   * @param b    Buffer to read into
   * @param off  Byte offset to read into
   * @param len  Number of bytes to read
   * @return     Number of bytes read, or <code>-1</code> if the end of
   *             the stream has been reached.
   * @exception  IOException  if an I/O error occurs.
   */
  @SuppressWarnings("cast")
  public int read(byte[] b, int off, int len)
    throws IOException 
  {
    // The first time through, scan the start of the file for a DOCTYPE
    // declaration.
    //
    if (firstTime) 
    {
      // Make sure we have a block of data to examine
      super.read(b, off, (len > BLOCK_SIZE - 1) ? (BLOCK_SIZE - 1) : len);

      // Do a sloppy job of converting it to a string.
      char[] cbuf = new char[count];
      for (int i = 0; i < count; i++)
        cbuf[i] = (char)(((int)buf[i]) & 0xff);
      String s = new String(cbuf);

      // Now look for a DOCTYPE declaration.
      int start = s.indexOf("<!DOCTYPE");
      if (start >= 0) 
      {
        int end = findEnd(s, start + 1);
        if (end >= 0) 
        {
          // We found one... change it into an XML comment.
          buf[start + 2] = '-';
          buf[start + 3] = '-';
          for (int i = start + 4; i < end - 2; i++)
            buf[i] = 'z';
          buf[end - 1] = '-';
          buf[end - 2] = '-';
        }
      }

      cbuf = new char[count];
      for (int i = 0; i < count; i++)
        cbuf[i] = (char)(((int)buf[i]) & 0xff);
      s = new String(cbuf);

      // Reset the file position so the client will see the modified
      // data.
      //
      pos = 0;
      firstTime = false;
    }

    return super.read(b, off, len);
  } // read( byte[], int, int )

  private int findEnd(String s, int start) 
  {
    int level = 0;
    char inQuote = 0;
    for (int i = start; i < s.length(); i++) 
    {
      char c = s.charAt(i);
      if (inQuote != 0 && c == inQuote)
        inQuote = 0;
      else if (c == '\'' || c == '\"')
        inQuote = c;
      else if (inQuote != 0)
        continue;
      else if (c == '<')
        level++;
      else if (c == '>') {
        level--;
        if (level < 0)
          return i;
      }
    }
    return -1;
  }
} // class DocTypeDeclRemover

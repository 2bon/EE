package net.dataninja.ee.util;


/**
net.dataninja copyright statement
 */
import java.io.IOException;
import java.io.OutputStream;

/**
 * This class is useful only for testing the transmission speed of data
 * by limiting the size of the output stream. Not to be used in production.
 */
class LimitedOutputStream extends OutputStream 
{
  /**
   * Constructor.
   *
   * @param realOut       The output stream to receive the limited output
   * @param limit         How many characters to limit it to.
   */
  public LimitedOutputStream(OutputStream realOut, int limit) {
    this.realOut = realOut;
    this.limit = limit;
  }

  /** Close the output stream */
  public void close()
    throws IOException 
  {
    realOut.close();
  }

  /** Flush any pending data to the output stream */
  public void flush()
    throws IOException 
  {
    realOut.flush();
  }

  /** Write an array of bytes to the output stream */
  public void write(byte[] b)
    throws IOException 
  {
    write(b, 0, b.length);
  }

  /** Write a subset of bytes to the stream */
  public void write(byte[] b, int off, int len)
    throws IOException 
  {
    int max = limit - total;
    if (max > 0) {
      if (max > len)
        max = len;
      realOut.write(b, off, max);
      total += max;
    }
  }

  /** Write a single byte to the stream */
  public void write(int b)
    throws IOException 
  {
    if (total < limit) {
      realOut.write(b);
      ++total;
    }
  }

  /** The output stream to receive the limited output */
  private OutputStream realOut;

  /** How many bytes have been output so far */
  private int total = 0;

  /** The limit on the number of bytes */
  private int limit;
} // class LimitedOutputStream

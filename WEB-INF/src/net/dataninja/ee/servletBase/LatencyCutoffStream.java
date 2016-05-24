package net.dataninja.ee.servletBase;


/**
net.dataninja copyright statement
 */
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletOutputStream;
import net.dataninja.ee.util.Trace;

/**
 * This class prints out latency information after a given number of bytes
 * have been output.
 */
class LatencyCutoffStream extends ServletOutputStream 
{
  /**
   * Constructor.
   *
   * @param realOut       The output stream to receive the limited output
   * @param limit         How many characters to output the message after
   * @param url           The URL of the request being served
   */
  public LatencyCutoffStream(OutputStream realOut, int limit,
                             long reqStartTime, String url) 
  {
    this.realOut = realOut;
    this.limit = limit;
    this.url = url;
    this.reqStartTime = reqStartTime;
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
    if (!isReported) 
    {
      if (total > limit)
        reportLatency();
      
      // If a chunk is being written that will put us over the limit,
      // first dump the part before the limit, then report the cutoff 
      // latency, then dump the chunk after.
      //
      else if (total+len > limit)
      {
      	assert len > 0;
        int beforeLen = limit - total;
        assert beforeLen > 0 && beforeLen < len;
        realOut.write(b, off, beforeLen);
        total += beforeLen;
        off += beforeLen;
        len -= beforeLen;
        reportLatency();
      }
    }
    realOut.write(b, off, len);
    total += len;
  }

  /** Write a single byte to the stream */
  public void write(int b)
    throws IOException 
  {
    if (!isReported && total > limit)
      reportLatency();
    realOut.write(b);
    ++total;
  }

  /** Tells whether the latency was reported yet */
  public boolean isReported() {
    return isReported;
  }

  /** Report the latency and set the flag saying it has been done. */
  private void reportLatency() {
    isReported = true;
    long latency = System.currentTimeMillis() - reqStartTime;
    Trace.info("Latency (cutoff): " + latency + " msec for request: " + url);
  }

  /** The output stream to receive the output */
  private OutputStream realOut;

  /** How many bytes have been output so far */
  private int total = 0;

  /** The limit on the number of bytes after which the message is printed */
  private int limit;

  /** The URL of the request being served */
  private String url;

  /** The start of the request, for timing purposes */
  private long reqStartTime;

  /** Whether the message has been printed yet */
  private boolean isReported = false;
} // class LatencyCutoffStream

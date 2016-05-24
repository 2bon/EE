package net.dataninja.ee.util;


/*
net.dataninja copyright statement
 */
import java.io.IOException;
import java.io.Writer;

/**
 * This is a simple PrintStream derivative that sends its output to the
 * ee Trace class instead of stdout or stderr.
 */
public class TraceWriter extends Writer 
{
  /** What level to output messages at */
  private int traceLevel;

  /** Buffer to build up each line, flushed at newline */
  private StringBuffer buf = new StringBuffer();

  /**
   * Construct a TraceWriter, recording the Trace level that future
   * messages written to the stream will be output at.
   *
   * @param traceLevel  Level to output future messages.
   */
  public TraceWriter(int traceLevel) {
    this.traceLevel = traceLevel;
  } // constructor

  /** Write a series of characters. Each newline-separated line is written
   *  to the Trace stream at the configured debug level.
   */
  public void write(char[] cbuf, int off, int len)
    throws IOException 
  {
    for (int i = 0; i < len; i++) 
    {
      char c = cbuf[i + off];
      if (c == '\n') {
        output(buf.toString());
        buf.setLength(0);
      }
      else
        buf.append(c);
    }
  } // write()

  /** Output a string at the configured debug level */
  private void output(String str) 
  {
    switch (traceLevel) {
      case Trace.debug:
        Trace.debug(str);
        break;
      case Trace.info:
        Trace.info(str);
        break;
      case Trace.warnings:
        Trace.warning(str);
        break;
      case Trace.errors:
        Trace.error(str);
        break;
      default:
        assert false : "Unrecognized trace level";
    }
  } // output()

  /** Flush any remaining output in the buffer */
  public void flush()
    throws IOException 
  {
    if (buf.length() > 0) {
      output(buf.toString());
      buf.setLength(0);
    }
  } // flush()

  /** Close the stream */
  public void close()
    throws IOException 
  {
    flush();
  } // close()
} // class TraceWriter

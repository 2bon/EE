package net.dataninja.ee.test;


/**
net.dataninja copyright statement
 */
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.servlet.ServletOutputStream;

/**
 * Used to gather output from dynaXML or crossQuery during a test.
 *
 * @author Rick Li
 */
public class FakeOutputStream extends ServletOutputStream 
{
  ByteArrayOutputStream buf = new ByteArrayOutputStream();

  public void write(int b)
    throws IOException 
  {
    buf.write(b);
  }

  public int length() {
    return buf.size();
  }

  public String toString() {
    return buf.toString();
  }
} // class FakeOutputStream

package net.dataninja.ee.test;


/**
dataninja copyright statement
 */
import java.io.IOException;
import javax.servlet.ServletOutputStream;

/**
 * Used to simulate gathering (but actually throw away) output from dynaXML or 
 * crossQuery during a test.
 *
 * @author Rick Li
 */
public class NullOutputStream extends ServletOutputStream 
{
  public void write(int b) throws IOException {
  }

  public int length() {
    return 0;
  }

  public String toString() {
    return "";
  }
} // class NullOutputStream

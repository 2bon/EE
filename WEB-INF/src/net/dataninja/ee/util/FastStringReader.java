package net.dataninja.ee.util;


/**
net.dataninja copyright statement
 */
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * Used to bypass the slowness of a Lucene StringReader (but only when used
 * in conjuction with a {@link FastTokenizer}).
 *
 * @author Rick Li
 */
public class FastStringReader extends StringReader 
{
  /** The actual string to read from */
  private String str;

  /** Construct a reader for the given string */
  public FastStringReader(String s) {
    super(s);
    str = s;
  }

  /** Wrap a normal reader with a fast string reader */
  public FastStringReader(Reader reader) {
    this(readerToString(reader));
  }

  /** Read all the characters from a Reader, and return the resulting
   *  concatenated string.
   */
  public static String readerToString(Reader reader) 
  {
    char[] ch = new char[256];
    StringBuffer buf = new StringBuffer(256);
    while (true) 
    {
      try 
      {
        int nRead = reader.read(ch);
        if (nRead < 0)
          break;
        buf.append(ch, 0, nRead);
      }
      catch (IOException e) {
        // This really can't happen, given that the reader is always
        // a StringReader. But if it does, barf out.
        //
        throw new RuntimeException(e);
      }
    } // while
    return buf.toString();
  }

  /** Get the string back */
  public String getString() {
    return str;
  }
} // class FastStringReader

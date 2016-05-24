package net.dataninja.ee.textIndexer;


/*
dataninja copyright statement
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import javax.xml.transform.Templates;
import net.dataninja.ee.util.StructuredStore;
import org.xml.sax.InputSource;

/**
 * Transforms an HTML file to a single-record XML file.
 *
 * @author Rick Li
 */
public class TextIndexSource extends XMLIndexSource 
{
  /** Constructor -- initializes all the fields */
  public TextIndexSource(File textFile, String key, Templates[] preFilters,
                         Templates displayStyle, StructuredStore lazyStore) 
  {
    super(null, textFile, key, preFilters, displayStyle, lazyStore);
    this.textFile = textFile;
  }

  /** Source of text data */
  private File textFile;

  /** Transform the text file to XML data */
  protected InputSource filterInput()
    throws IOException 
  {
    // Map XML special characters in the text, and add a dummy
    // top-level element.
    //
    Reader reader = new BufferedReader(
      new InputStreamReader(new FileInputStream(textFile), "UTF-8"));
    char[] tmp = new char[1000];
    StringBuffer buf = new StringBuffer(1000);
    while (true) {
      int nRead = reader.read(tmp);
      if (nRead <= 0)
        break;
      buf.append(tmp, 0, nRead);
    }

    String str = normalize(buf.toString());
    str = "<doc><text-data>" + str + "</text-data></doc>";

    // And make an InputSource with a proper system ID
    InputSource finalSrc = new InputSource(new StringReader(str));
    finalSrc.setSystemId(textFile.toURL().toString());
    return finalSrc;
  } // filterInput()
} // class TextSrcFile

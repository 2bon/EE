package net.dataninja.ee.textIndexer;


/*
dataninja copyright statement
 *
 * Acknowledgements:
 *
 * A significant amount of new and/or modified code in this module
 * was made possible by a grant from the Andrew W. Mellon Foundation,
 * as part of the Melvyl Recommender Project.
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import javax.xml.transform.Templates;
import net.dataninja.ee.util.StructuredStore;
import org.xml.sax.InputSource;

/**
 * Transforms an HTML file to a single-record XML file.
 *
 * @author Rick Li
 */
public class HTMLIndexSource extends XMLIndexSource 
{
  /** Constructor -- initializes all the fields */
  public HTMLIndexSource(File htmlFile, String key, Templates[] preFilters,
                         Templates displayStyle, StructuredStore lazyStore) 
  {
    super(null, htmlFile, key, preFilters, displayStyle, lazyStore);
    this.htmlFile = htmlFile;
  }

  /** Source of HTML data */
  private File htmlFile;

  /** Transform the HTML file to XML data */
  protected InputSource filterInput()
    throws IOException 
  {
    // Convert the HTML file into an XML string that we can index.
    InputStream inStream = new FileInputStream(htmlFile);
    String htmlXMLStr = HTMLToString.convert(inStream);

    // And make an InputSource with a proper system ID
    InputSource finalSrc = new InputSource(new StringReader(htmlXMLStr));
    finalSrc.setSystemId(htmlFile.toURL().toString());
    return finalSrc;
  } // filterInput()
} // class HTMLIndexSource

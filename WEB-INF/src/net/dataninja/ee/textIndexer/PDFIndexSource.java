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
 * Transforms a PDF file to a single-record XML file.
 *
 * @author Rick Li
 */
public class PDFIndexSource extends XMLIndexSource 
{
  /** Constructor -- initializes all the fields */
  public PDFIndexSource(File pdfFile, String key, Templates[] preFilters,
                        Templates displayStyle, StructuredStore lazyStore) 
  {
    super(null, pdfFile, key, preFilters, displayStyle, lazyStore);
    this.pdfFile = pdfFile;
  }

  /** Source of PDF data */
  private File pdfFile;

  /** Transform the PDF file to XML data */
  protected InputSource filterInput()
    throws IOException 
  {
    // Convert the PDF file into an XML string that we can index.
    InputStream inStream = new FileInputStream(pdfFile);
    String pdfXMLStr = PDFToString.convert(inStream);

    // And make an InputSource with a proper system ID
    InputSource finalSrc = new InputSource(new StringReader(pdfXMLStr));
    finalSrc.setSystemId(pdfFile.toURL().toString());
    return finalSrc;
  } // filterInput()
} // class PDFSrcFile

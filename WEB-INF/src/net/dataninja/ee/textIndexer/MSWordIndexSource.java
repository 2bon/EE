package net.dataninja.ee.textIndexer;

/*
dataninja copyright statement
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.StringTokenizer;

import javax.xml.transform.Templates;

import net.dataninja.ee.util.StructuredStore;
import org.textmining.extraction.TextExtractor;
import org.textmining.extraction.word.WordTextExtractorFactory;
import org.xml.sax.InputSource;

/**
 * Transforms a Microsoft Word file to a single-record XML file.
 *
 * @author Rick Li
 */
public class MSWordIndexSource extends XMLIndexSource 
{
  /** Constructor -- initializes all the fields */
  public MSWordIndexSource(File msWordFile, String key, Templates[] preFilters,
                           Templates displayStyle, StructuredStore lazyStore) 
  {
    super(null, msWordFile, key, preFilters, displayStyle, lazyStore);
    this.msWordFile = msWordFile;
  }

  /** Source of MS Word document data */
  private File msWordFile;

  /** Transform the MS Word file to XML data */
  protected InputSource filterInput()
    throws IOException 
  {
    // Open the Word file and see if we can understand it.
    InputStream inStream = new FileInputStream(msWordFile);
    try 
    {
      // Try to extract the text.
      TextExtractor extractor = new WordTextExtractorFactory().textExtractor(inStream);
      String str = extractor.getText();

      // Break it up into paragraphs.
      StringBuffer outBuf = new StringBuffer((int) msWordFile.length());
      outBuf.append("<rippedMSWordText>\n");
      StringTokenizer st = new StringTokenizer(str, "\r\t", false);
      while (st.hasMoreTokens()) {
        String para = st.nextToken().trim();
        // Remove invalid Unicode chars, escape ampersands & stuff.
        para = normalize(para);
        if (para.length() > 0) {
          outBuf.append("  <p>" + para + "</p>\n");
        }
      }
      outBuf.append("</rippedMSWordText>\n");

      // And make an InputSource with a proper system ID
      InputSource finalSrc = new InputSource(new StringReader(outBuf.toString()));
      finalSrc.setSystemId(msWordFile.toURL().toString());
      return finalSrc;
    }
    catch (IOException e) {
      throw e;
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
    finally {
      inStream.close();
    }

  } // filterInput()
} // class MSWordIndexSource

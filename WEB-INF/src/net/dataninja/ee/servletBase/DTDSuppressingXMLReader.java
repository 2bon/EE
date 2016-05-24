package net.dataninja.ee.servletBase;


/*
dataninja copyright statement
 */
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

/**
 * Simple wrapper for an XML reader that requests it to avoid loading external
 * DTDs. This not only speeds things up, it also allows our service to work
 * even if the external service is unavailable.
 *
 * @author Rick Li
 */
public class DTDSuppressingXMLReader implements XMLReader 
{
  /** The wrapped XML reader to which all methods are delegated */
  protected XMLReader reader;

  /**
   * Construct the XML reader and set a flag on it to avoid loading
   * external DTDs
   */
  public DTDSuppressingXMLReader() 
  {
    // First, create the reader.
    try {
      reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
    }
    catch (ParserConfigurationException err) {
      throw new TransformerFactoryConfigurationError(err);
    }
    catch (SAXException err) {
      throw new TransformerFactoryConfigurationError(err);
    }

    // Then ask it to not load external DTDs (unless validating.)
    try 
    {
      reader.setFeature(
        "http://apache.org/xml/features/nonvalidating/load-external-dtd",
        false);
    }
    catch (SAXNotSupportedException err) {
      // ignore
    }
    catch (SAXNotRecognizedException err) {
      // ignore
    }
  }

  ////////////////////////////////////////////////////////////////////////////
  // Delegated methods
  ////////////////////////////////////////////////////////////////////////////
  public boolean equals(Object obj) {
    return reader.equals(obj);
  }

  public ContentHandler getContentHandler() {
    return reader.getContentHandler();
  }

  public DTDHandler getDTDHandler() {
    return reader.getDTDHandler();
  }

  public EntityResolver getEntityResolver() {
    return reader.getEntityResolver();
  }

  public ErrorHandler getErrorHandler() {
    return reader.getErrorHandler();
  }

  public boolean getFeature(String name)
    throws SAXNotRecognizedException, SAXNotSupportedException 
  {
    return reader.getFeature(name);
  }

  public Object getProperty(String name)
    throws SAXNotRecognizedException, SAXNotSupportedException 
  {
    return reader.getProperty(name);
  }

  public int hashCode() {
    return reader.hashCode();
  }

  public void parse(String systemId)
    throws IOException, SAXException 
  {
    reader.parse(systemId);
  }

  public void parse(InputSource input)
    throws IOException, SAXException 
  {
    reader.parse(input);
  }

  public void setContentHandler(ContentHandler handler) {
    reader.setContentHandler(handler);
  }

  public void setDTDHandler(DTDHandler handler) {
    reader.setDTDHandler(handler);
  }

  public void setEntityResolver(EntityResolver resolver) {
    reader.setEntityResolver(resolver);
  }

  public void setErrorHandler(ErrorHandler handler) {
    reader.setErrorHandler(handler);
  }

  public void setFeature(String name, boolean value)
    throws SAXNotRecognizedException, SAXNotSupportedException 
  {
    reader.setFeature(name, value);
  }

  public void setProperty(String name, Object value)
    throws SAXNotRecognizedException, SAXNotSupportedException 
  {
    reader.setProperty(name, value);
  }

  public String toString() {
    return reader.toString();
  }
} // class DTDSuppressingXMLReader

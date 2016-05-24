package net.dataninja.ee.dynaXML;

import java.io.IOException;
import javax.xml.transform.Templates;
import net.dataninja.ee.servletBase.TextServlet;
import net.dataninja.ee.util.StructuredStore;
import org.xml.sax.InputSource;

/*
net.dataninja copyright statement
 */

/*
 * This file created on Mar 11, 2005 by Rick Li
 */

/**
 * Iterface that locates lazy or normal data streams for dynaXML document
 * requests. The default implementation, {@link DefaultDocLocator}, implements
 * local file access, but other implementations can be imagined that
 * read/write files over the network.
 *
 * @author Rick Li
 */
public interface DocLocator 
{
  /**
   * Attach this locator to a specific servlet, which can be used to
   * provide, among other thigns, path mapping services.
   *
   * @param servlet   Servlet to attach to
   */
  void setServlet(TextServlet servlet);

  /**
   * Search for a StructuredStore containing the "lazy" or persistent
   * representation of a given document. Index parameters are specified,
   * since often the lazy file is stored along with the index. This method
   * is called first, and if it returns null, then
   * {@link #getInputSource(String, boolean)} will be called as a fall-back.
   *
   * @param sourcePath      Path to the source document
   * @param indexConfigPath Path to the index configuration file
   * @param indexName       Name of the index being searched
   * @param preFilter       Stylesheet to filter the document with
   * @param removeDoctypeDecl Set to true to remove DOCTYPE declaration from
   *                          the XML document.
   *
   * @return                Store containing the tree, or null if none
   *                        could be found.
   */
  StructuredStore getLazyStore(String indexConfigPath, String indexName,
                               String sourcePath, Templates preFilter,
                               boolean removeDoctypeDecl)
    throws IOException;

  /**
   * Retrieve the data stream for an XML source document.
   *
   * @param sourcePath  Path to the source document
   * @param removeDoctypeDecl Set to true to remove DOCTYPE declaration from
   *                          the XML document.
   *
   * @return            Data stream for the document.
   */
  InputSource getInputSource(String sourcePath, boolean removeDoctypeDecl)
    throws IOException;
} // interface DocLocator

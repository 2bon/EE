package net.dataninja.ee.dynaXML.test;


/**
net.dataninja copyright statement
 */
import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;

import net.dataninja.ee.dynaXML.DefaultDocLocator;
import net.dataninja.ee.dynaXML.DocLocator;
import net.dataninja.ee.dynaXML.DocRequest;
import net.dataninja.ee.dynaXML.DynaXML;
import net.dataninja.ee.dynaXML.InvalidDocumentException;
import net.dataninja.ee.lazyTree.SearchTree;
import net.dataninja.ee.servletBase.TextConfig;
import net.dataninja.ee.test.FakeServletConfig;
import net.dataninja.ee.test.FakeServletContext;
import net.dataninja.ee.test.FakeServletRequest;
import net.dataninja.ee.test.FakeServletResponse;
import net.dataninja.ee.test.NullOutputStream;
import net.dataninja.ee.textEngine.DefaultQueryProcessor;
import net.dataninja.ee.textEngine.IndexUtil;
import net.dataninja.ee.textEngine.IndexWarmer;
import net.dataninja.ee.textEngine.QueryProcessor;
import net.dataninja.ee.textIndexer.IndexInfo;
import net.dataninja.ee.util.AttribList;
import org.xml.sax.SAXException;

/**
 * Extends the DynaXML servlet slightly to allow programmatic testing of
 * the servlet. Authentication always succeeds. Any exceptions will be
 * thrown upward rather than generating an error page.
 *
 * @author Rick Li
 */
public class TestableDynaXML extends DynaXML 
{
  private String baseDir;
  private String indexDirOverride;
  private IndexWarmer indexWarmer;
  private ThreadLocal<Integer> nHits = new ThreadLocal<Integer>();
  
  /**
   * Simplified initialization for use outside a real servlet container.
   * 
   * @param baseDir the ee home directory.
   * @throws ServletException if anything goes wrong.
   */
  public TestableDynaXML(String baseDir) throws ServletException
  {
    this.baseDir = baseDir;
    FakeServletContext context = new FakeServletContext();
    FakeServletConfig config = new FakeServletConfig(context, baseDir, "dynaXML");
    super.init(config);
  }
  
  /** Allows overriding the directory specified in future query requests. */
  public void overrideIndexDir(String dir) {
    indexDirOverride = dir;
  }
  
  /** Allows overriding default index warmer. */
  public void setIndexWarmer(IndexWarmer warmer) {
    indexWarmer = warmer;
  }
  
  /** Return the number of hits in the last request processed by this thread */
  public int nHits() { return nHits.get(); }
  
  /** For test mode, do nothing to the current trace flags. */
  @Override
  protected void setupTrace(TextConfig config) { }
  
  /** Allow overriding the index directory */
  @Override
  protected DocRequest runDocReqParser(HttpServletRequest req,
                                       AttribList attribs)
    throws Exception
  {
    DocRequest docRequest = super.runDocReqParser(req, attribs);
    if (indexDirOverride != null && docRequest.query != null)
      docRequest.query.indexPath = indexDirOverride;
    return docRequest;
  }

  /** For test mode, allow override of index warmer. Default to foreground warming. */
  @Override
  public QueryProcessor createQueryProcessor()
  {
    DefaultQueryProcessor processor = new DefaultQueryProcessor();
    processor.setXtfHome(baseDir);
    if (indexWarmer == null)
      indexWarmer = new IndexWarmer(baseDir, 0);
    processor.setIndexWarmer(indexWarmer);
    return processor;
  }

  /**
   * Simplified method to test-get the given URL. Throws away the output
   * but retains the number of hits.
   * 
   * @param url the URL to test-get
   */
  public void service(String url) throws ServletException, IOException
  {
    FakeServletRequest req = new FakeServletRequest(url);
    NullOutputStream out = new NullOutputStream();
    FakeServletResponse res = new FakeServletResponse(out);
    super.service(req, res);
  }

  /**
   * Perform the normal dynaXML getSourceDoc() operation, then record the
   * resulting number of hits (if any) that came out.
   */
  @Override
  protected Source getSourceDoc(DocRequest docReq, Transformer transformer) 
    throws InvalidDocumentException, IOException, SAXException, ParserConfigurationException
  {
    Source src = super.getSourceDoc(docReq, transformer);
    if (src instanceof SearchTree)
      nHits.set(((SearchTree)src).getTotalHits());
    else
      nHits.set(0);
    return src;
  }
  
  /**
   * Performs user authentication for a request, given the authentication
   * info for the document. In the case of testing, we never fail
   * authentication.
   */
  @Override
  protected boolean authenticate(DocRequest docReq,
                                 HttpServletRequest req, HttpServletResponse res)
    throws Exception 
  {
    return true;
  }

  /**
   * Would normally generate an error page. Instead we throw an exception
   * upward.
   */
  @Override
  protected void genErrorPage(HttpServletRequest req, HttpServletResponse res,
                              Exception exc) 
  {
    throw new RuntimeException(exc);
  } // genErrorPage()
  
  @Override
  public DocLocator createDocLocator() 
  {
    DocLocator loc = new DefaultDocLocator() 
    {
      @Override
      public File calcLazyPath(File xtfHome, File idxConfigFile,
                               String idxName, File srcTextFile,
                               boolean createDir) throws IOException
      {
        // If no index directory override, do the usual thing.
        if (indexDirOverride == null)
          return super.calcLazyPath(xtfHome, idxConfigFile, idxName, srcTextFile, createDir);
        
        // First, load the particular index info from the config file (though if
        // we've already loaded it, the cache will just return it.)
        //
        try {
          IndexInfo idxInfo = IndexUtil.getIndexInfo(idxConfigFile, idxName);
          String oldIndexPath = idxInfo.indexPath;
          try {
            // Temporarily override the path.
            idxInfo.indexPath = indexDirOverride;
            
            // Use the other form of calcLazyPath() to do the rest of the work.
            return IndexUtil.calcLazyPath(xtfHome, idxInfo, srcTextFile, createDir);
          }
          finally {
            // Restore the path in the index info.
            idxInfo.indexPath = oldIndexPath;
          }
        }
        catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    };
    
    loc.setServlet(this);
    return loc;
  }
} // class TestableDynaXML

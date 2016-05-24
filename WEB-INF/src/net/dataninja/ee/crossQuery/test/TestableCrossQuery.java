package net.dataninja.ee.crossQuery.test;


/**
net.dataninja copyright statement
 */
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.dataninja.ee.crossQuery.CrossQuery;
import net.dataninja.ee.crossQuery.QueryRoute;
import net.dataninja.ee.servletBase.TextConfig;
import net.dataninja.ee.test.FakeServletConfig;
import net.dataninja.ee.test.FakeServletContext;
import net.dataninja.ee.test.FakeServletRequest;
import net.dataninja.ee.test.FakeServletResponse;
import net.dataninja.ee.test.NullOutputStream;
import net.dataninja.ee.textEngine.DefaultQueryProcessor;
import net.dataninja.ee.textEngine.IndexWarmer;
import net.dataninja.ee.textEngine.QueryProcessor;
import net.dataninja.ee.textEngine.QueryRequest;
import net.dataninja.ee.textEngine.QueryResult;
import net.dataninja.ee.util.AttribList;

/**
 * Derived version of the crossQuery servlet, used to abuse crossQuery during
 * load tests. The main difference is that it throws exceptions upward instead
 * of formatting an error page. This ensures that exceptions don't get hidden
 * in the noise.
 * 
 * Also, for each thread we track the number of hits returned by the last
 * request.
 *
 * @author Rick Li
 */
public class TestableCrossQuery extends CrossQuery 
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
  public TestableCrossQuery(String baseDir) throws ServletException
  {
    this.baseDir = baseDir;
    FakeServletContext context = new FakeServletContext();
    FakeServletConfig config = new FakeServletConfig(context, baseDir, "crossQuery");
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
  protected QueryRequest runQueryParser(HttpServletRequest req,
                                        HttpServletResponse res,
                                        QueryRoute route, 
                                        AttribList attribs)
    throws Exception 
  {
    QueryRequest queryReq = super.runQueryParser(req, res, route, attribs);
    if (indexDirOverride != null)
      queryReq.indexPath = indexDirOverride;
    return queryReq;
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

  // inherit Javadoc
  @Override
  protected void formatHits(String mainTagName, HttpServletRequest req,
                            HttpServletResponse res, AttribList attribs,
                            QueryRequest queryRequest, QueryResult queryResult,
                            long startTime)
    throws Exception 
  {
    nHits.set(queryResult.totalDocs);
    super.formatHits(mainTagName,
                     req,
                     res,
                     attribs,
                     queryRequest,
                     queryResult,
                     startTime);
  }

  // inherit Javadoc
  @Override
  protected void genErrorPage(HttpServletRequest req, HttpServletResponse res,
                              Exception exc) 
  {
    nHits.set(-1);
    throw new RuntimeException(exc);
  } // genErrorPage()
} // class TestableCrossQuery

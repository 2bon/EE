package net.dataninja.ee.crossQuery.raw;


/**
dataninja copyright statement
 */
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.SocketException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamSource;

import net.dataninja.ee.crossQuery.CrossQuery;
import net.dataninja.ee.crossQuery.CrossQueryConfig;
import net.dataninja.ee.servletBase.RedirectException;
import net.dataninja.ee.servletBase.TextConfig;
import net.dataninja.ee.textEngine.QueryProcessor;
import net.dataninja.ee.textEngine.QueryRequest;
import net.dataninja.ee.textEngine.QueryRequestParser;
import net.dataninja.ee.textEngine.QueryResult;
import net.dataninja.ee.util.AttribList;

/**
 * Derived version of the crossQuery servlet, used to provide a "web service"
 * interface to ee. Takes an HTTP post containing a single ee query in XML,
 * parses the request, executes the query, and returns raw XML-formatted 
 * results. 
 *
 * @author Rick Li
 */
public class RawQuery extends CrossQuery 
{
  /** 
   * We're keeping this servlet intentionally very simple -- so no config file.
   */
  protected TextConfig readConfig(String configPath) 
  {
    config = new RawQueryConfig(this);
    return config;
  }
  
  // inherit JavaDoc
  public String getConfigName() {
    return "no config file";
  }
  
  /**
   * Handles the HTTP 'get' and 'put' methods. Initializes the servlet if 
   * nececssary, then parses the HTTP request and processes it appropriately.
   *
   * @param     req            The HTTP request (in)
   * @param     res            The HTTP response (out)
   * @exception IOException    If unable to read an index or data file, or
   *                           if unable to write the output stream.
   */
  public void doGet(HttpServletRequest req, HttpServletResponse res)
    throws IOException 
  {
    try 
    {
      // Set the default output content type
      res.setContentType("text/xml");

      // If an error occurs, be sure to just format it "raw" (don't use an
      // error generator stylesheet)
      //
      req.setAttribute("net.dataninja.ee.servlet.raw", "1");
      
      // This does the bulk of the work.
      apply(req, res);
    }
    catch (Exception e) {
      if (!(e instanceof SocketException)) 
      {
        try {
          genErrorPage(req, res, e);
        }
        catch (RedirectException re) {
        }
      }
      return;
    }
  } // doGet()

  // inherit JavaDoc
  public String getServletInfo() {
    return "rawQuery search servlet";
  } // getServletInfo()

  /**
  * Creates the query request, processes it, and formats the results.
  *
  * @param req        The original HTTP request
  * @param res        Where to send the response
  *
  * @exception Exception  Passes on various errors that might occur.
  */
  protected void apply(HttpServletRequest req,
                       HttpServletResponse res)
    throws Exception 
  {
    // Record the start time.
    long startTime = System.currentTimeMillis();

    // Grab the "query" parameter -- it must be present.
    String queryText = req.getParameter("query");
    if (queryText == null || queryText.length() == 0)
      throw new RuntimeException("'query' parameter must be specified");
    
    // Parse the XML query to make an ee QueryRequest
    QueryRequest queryReq = new QueryRequestParser().parseRequest(
      new StreamSource(new StringReader(queryText)),
      new File(getRealPath("")));

    // Fill in the auxiliary info
    queryReq.parserInput = null;
    queryReq.parserOutput = queryText;

    // Process it to generate result document hits
    QueryProcessor proc = createQueryProcessor();
    QueryResult queryResult = proc.processRequest(queryReq);

    // Format the hits for the output document.
    formatHits("crossQueryResult",
               req,
               res,
               new AttribList(),
               queryReq,
               queryResult,
               startTime);
  } // apply()

  private class RawQueryConfig extends CrossQueryConfig
  {
    RawQueryConfig(RawQuery servlet) { super(servlet); }
  }} // class TestableCrossQuery

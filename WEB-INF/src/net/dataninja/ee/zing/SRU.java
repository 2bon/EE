package net.dataninja.ee.zing;


/**
net.dataninja copyright statement
 */
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;
import net.sf.saxon.om.NodeInfo;
import net.dataninja.ee.crossQuery.CrossQuery;
import net.dataninja.ee.crossQuery.QueryRoute;
import net.dataninja.ee.servletBase.TextConfig;
import net.dataninja.ee.textEngine.QueryProcessor;
import net.dataninja.ee.textEngine.QueryRequest;
import net.dataninja.ee.textEngine.QueryResult;
import net.dataninja.ee.util.AttribList;
import net.dataninja.ee.util.EasyNode;
import net.dataninja.ee.util.XMLWriter;

/**
 * The SRU servlet coordinates the process of parsing a URL query,
 * activating the textEngine to find all occurrences, and finally formatting
 * the results.
 */
public class SRU extends CrossQuery 
{
  // inherit JavaDoc
  public String getConfigName() {
    return "conf/sru.conf";
  }

  // inherit JavaDoc
  protected TextConfig readConfig(String configPath) 
  {
    // Load the configuration file.
    config = new SRUConfig(this, configPath);

    // And we're done.
    return config;
  } // readConfig()

  // inherit JavaDoc
  public TextConfig getConfig() {
    return config;
  }

  // inherit JavaDoc
  public String getServletInfo() {
    return "SRU servlet";
  } // getServletInfo()

  // inherit JavaDoc
  protected void apply(AttribList attribs, HttpServletRequest req,
                       HttpServletResponse res)
    throws Exception 
  {
    // Record the start time.
    long startTime = System.currentTimeMillis();

    // Switch the default output mode to XML.
    res.setContentType("text/xml");

    // Make a default route, but set up to do CQL parsing on the query
    // parameter instead of the default tokenization.
    //
    QueryRoute route = QueryRoute.createDefault(config.queryParserSheet);
    route.tokenizerMap.put("query", "CQL");

    // Generate a query request document from the queryParser stylesheet.
    QueryRequest queryReq = runQueryParser(req, res, route, attribs);
    if (queryReq == null)
      return;

    // Process it to generate result document hits
    QueryProcessor proc = createQueryProcessor();
    QueryResult result = proc.processRequest(queryReq);

    // Format the hits for the output document. Include the <parameters> block
    // and the actual query request, in case the stylesheet wants to use these
    // things.
    //
    formatHits("SRUResult", req, res, attribs, queryReq, result, startTime);
  }

  /**
   * Called right after the raw query request has been generated, but
   * before it is parsed. Gives us a chance to stop processing here in
   * if SRW diagnostics should be output instead of running a query.
   */
  protected boolean shuntQueryReq(HttpServletRequest req,
                                  HttpServletResponse res, Source queryReqDoc)
    throws IOException 
  {
    // If it actually contains an SRW explain response, or an SRW
    // diagnostic, simply output that directly.
    //
    EasyNode node = new EasyNode((NodeInfo)queryReqDoc);
    if (directOutput(node, "diagnostics", res))
      return true;
    if (directOutput(node, "explainResponse", res))
      return true;

    return super.shuntQueryReq(req, res, queryReqDoc);
  } // shuntQueryReq()

  /** Add additional stuff to the usual debug step mode */
  protected String stepSetup(HttpServletRequest req, HttpServletResponse res)
    throws IOException 
  {
    String stepStr = super.stepSetup(req, res);
    if (stepStr != null) {
      stepStr = stepStr.replaceAll("crossQuery", "SRU");
      String step = req.getParameter("debugStep");
      if (step.equals("2a"))
        stepStr = stepStr.replaceAll("Next,",
                                     "Note that the 'query' parameter has been " +
                                     "parsed as CQL. Next,");
      stepStr = stepStr.replaceAll("final HTML", "final SRW-formatted XML");
      stepStr = stepStr.replaceAll("XML page", "XML result");
    }

    return stepStr;
  }

  /**
   * Scans the node and its descendants for an SRW 'explainResponse' or
   * 'diagnostics'. If found, it is output directly.
   *
   * @param node    Node to scan
   * @param name    Name to scan for
   * @return        true if direct output was made
   */
  private boolean directOutput(EasyNode node, String name,
                               HttpServletResponse res)
    throws IOException 
  {
    // If the node is an explainResponse or diagnostic, output it
    // directly.
    //
    if (name.equals(node.name())) {
      String strVal = XMLWriter.toString(node);
      res.getWriter().print(strVal);
      return true;
    }

    // Scan the children.
    for (int i = 0; i < node.nChildren(); i++) {
      if (directOutput(node.child(i), name, res))
        return true;
    }

    // None found in this branch.
    return false;
  } // directOutput()
} // class SRU

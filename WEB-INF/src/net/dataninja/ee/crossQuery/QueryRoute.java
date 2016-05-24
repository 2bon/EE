package net.dataninja.ee.crossQuery;


/*
dataninja copyright statement
 */
import java.util.HashMap;
import net.sf.saxon.om.NodeInfo;
import net.dataninja.ee.util.EasyNode;
import net.dataninja.ee.util.GeneralException;

/**
 * Routes a request to a particular query parser. Optionally contains
 * special tokenizing instructions for one or more URL parameters.
 */
public class QueryRoute 
{
  /** Path to the query parser stylesheet */
  public String queryParserSheet;

  /** Optional: path to the error generator stylesheet */
  public String errorGenSheet;

  /** Special parsing requests for particular URL parameters */
  public HashMap tokenizerMap = new HashMap();

  /** Optional: input to query router stylesheet */
  public String routerInput = null;

  /** Optional: output from query router stylesheet */
  public String routerOutput = null;

  /** Do not construct directly -- use {@link #parse(NodeInfo)} */
  protected QueryRoute() {
  }

  /**
   * Create a default route to the given query parser
   */
  public static QueryRoute createDefault(String queryParserSheet) {
    QueryRoute ret = new QueryRoute();
    ret.queryParserSheet = queryParserSheet;
    return ret;
  } // createDefault()

  /**
   * Reads and parses the route output from a queryRouter stylesheet.
   *
   * @param  input               The output from a queryRouter stylesheet
   * @throws GeneralException    If a read or parse error occurs.
   */
  public static QueryRoute parse(NodeInfo input)
    throws GeneralException 
  {
    // Create the (empty) result
    QueryRoute ret = new QueryRoute();

    // Make sure the root tag is correct.
    EasyNode root = new EasyNode(input);
    String rootTag = root.name();
    if (rootTag.equals("") && root.nChildren() == 1) {
      root = root.child(0);
      rootTag = root.name();
    }

    if (!rootTag.equalsIgnoreCase("route"))
      throw new QueryRouteException(
        "Query router stylesheet must output a 'route' element");

    // Pick out the elements
    for (int i = 0; i < root.nChildren(); i++) 
    {
      EasyNode el = root.child(i);
      if (!el.isElement())
        continue;

      // Was a query parser specified?
      String tagName = el.name();
      if (tagName.equalsIgnoreCase("queryParser"))
        ret.parseQueryParser(el);
      else if (tagName.equalsIgnoreCase("errorGen"))
        ret.parseErrorGen(el);
      else if (tagName.equalsIgnoreCase("tokenize"))
        ret.parseTokenizer(el);
    } // for i

    // Make sure that required parameters were specified.
    if (ret.queryParserSheet == null || ret.queryParserSheet.length() == 0)
      throw new QueryRouteException(
        "Query router stylesheet must output a 'queryParser' element");

    return ret;
  } // parse()

  /**
   * Parse a 'queryParser' element
   */
  private void parseQueryParser(EasyNode el) 
  {
    // Scan each attribute of each element.
    for (int j = 0; j < el.nAttrs(); j++) 
    {
      if (el.attrName(j).equalsIgnoreCase("path"))
        queryParserSheet = el.attrValue(j);
      else {
        throw new GeneralException(
          "Query router attribute " + el.name() + "." + el.attrName(j) +
          " not recognized");
      }
    }
  } // parseQueryParser()

  /**
   * Parse a 'errorGen' element
   */
  private void parseErrorGen(EasyNode el) 
  {
    // Scan each attribute of each element.
    for (int j = 0; j < el.nAttrs(); j++) 
    {
      if (el.attrName(j).equalsIgnoreCase("path"))
        errorGenSheet = el.attrValue(j);
      else {
        throw new GeneralException(
          "Query router attribute " + el.name() + "." + el.attrName(j) +
          " not recognized");
      }
    }
  } // parseErrorGen()

  /**
   * Parse a 'tokenize' element
   */
  private void parseTokenizer(EasyNode el) 
  {
    String paramName = null;
    String tokenizer = null;

    // Scan each attribute of each element.
    for (int j = 0; j < el.nAttrs(); j++) 
    {
      if (el.attrName(j).equalsIgnoreCase("param"))
        paramName = el.attrValue(j);
      else if (el.attrName(j).equalsIgnoreCase("tokenizer"))
        tokenizer = el.attrValue(j);
      else {
        throw new GeneralException(
          "Query router attribute " + el.name() + "." + el.attrName(j) +
          " not recognized");
      }
    }

    // Make sure both specified.
    if (paramName == null || tokenizer == null)
      throw new GeneralException(
        el.name() +
        " element requires 'param' and 'tokenizer' attributes to be specified");

    // Add it.
    tokenizerMap.put(paramName, tokenizer);
  } // parseTokenizer()
} // class QueryRoute

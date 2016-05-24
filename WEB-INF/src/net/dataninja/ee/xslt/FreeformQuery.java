package net.dataninja.ee.xslt;

/*
dataninja copyright statement
 */

import java.io.StringReader;
import javax.xml.transform.stream.StreamSource;
import net.dataninja.ee.textEngine.freeform.FreeformQueryParser;
import net.dataninja.ee.textEngine.freeform.ParseException;
import net.dataninja.ee.textEngine.freeform.FreeformQueryParser.FNode;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.AllElementStripper;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.tinytree.TinyBuilder;
import net.sf.saxon.trans.XPathException;

/**
 * Utility function to parse a "freeform" google-style query into ee compatible
 * format.
 *
 * @author Rick Li
 */
public class FreeformQuery 
{
  /**
   * Driver for calling from Saxon. Returns result as a traversible XML tree.
   */
  public static NodeInfo parse(XPathContext context, String queryStr) 
    throws ParseException
  {
    FreeformQueryParser parser = new FreeformQueryParser(new StringReader(queryStr));
    FNode parsed = parser.Query();
    String strVersion = parsed.toXML();
    StreamSource src = new StreamSource(new StringReader(strVersion));
    try {
      return TinyBuilder.build(src, new AllElementStripper(), context.getConfiguration());
    }
    catch (XPathException e) {
      throw new RuntimeException(e);
    }
  }
}

package net.dataninja.ee.util;


/**
net.dataninja copyright statement
 */
import java.io.StringWriter;
import java.util.Properties;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import net.sf.saxon.FeatureKeys;
import net.sf.saxon.om.NodeInfo;

/**
 * Simple utility class that takes a Node or Source (representing an XML
 * document) and produces an indented string representation of it. This is
 * very useful for debugging.
 *
 * @author Rick Li
 */
public class XMLWriter 
{
  /**
   * Prints the node, in XML format, to Trace.debug()
   */
  public static void debug(EasyNode node) {
    Trace.debug(toString(node, true));
  }

  /**
   * Prints the node, in XML format, to Trace.debug()
   */
  public static void debug(EasyNode node, boolean includeXMLDecl) {
    Trace.debug(toString(node, includeXMLDecl));
  }

  /**
   * Prints the node, in XML format, to Trace.debug()
   */
  public static void debug(Source node) {
    Trace.debug(toString(node, true));
  } // debugNode()

  /**
   * Prints the node, in XML format, to Trace.debug()
   */
  public static void debug(Source node, boolean includeXMLDecl) {
    Trace.debug(toString(node, includeXMLDecl));
  } // debugNode()

  /**
   * Format a nice, multi-line, indented, representation of the given
   * XML fragment.
   *
   * @param node  Base node to format.
   */
  public static String toString(EasyNode node) {
    return toString(node.getWrappedNode(), true);
  }

  /**
   * Format a nice, multi-line, indented, representation of the given
   * XML fragment.
   *
   * @param node  Base node to format.
   */
  public static String toString(EasyNode node, boolean includeXMLDecl) {
    return toString(node.getWrappedNode(), includeXMLDecl);
  }

  /**
   * Format a nice, multi-line, indented, representation of the given
   * XML fragment.
   *
   * @param node  Base node to format.
   */
  public static String toString(Source node) {
    return toString(node, true);
  }

  /**
   * Format a nice, multi-line, indented, representation of the given
   * XML fragment.
   *
   * @param node  Base node to format.
   * @param includeXMLDecl true to include XML declaration, false to
   *                       suppress it (useful for generating a fragment
   *                       of XML to insert within a larger document.)
   */
  public static String toString(Source node, boolean includeXMLDecl) 
  {
    try 
    {
      StringWriter writer = new StringWriter();
      StreamResult tmp = new StreamResult(writer);
      TransformerFactory factory = new net.sf.saxon.TransformerFactoryImpl();

      // Avoid NamePool translation, as it triggers a Saxon bug. 
      if (node instanceof NodeInfo) {
        factory.setAttribute(FeatureKeys.NAME_POOL,
                             ((NodeInfo)node).getNamePool());
      }

      Transformer trans = factory.newTransformer();
      Properties props = trans.getOutputProperties();
      props.put("indent", "yes");
      props.put("method", "xml");
      if (!includeXMLDecl)
        props.put("omit-xml-declaration", "yes");
      trans.setOutputProperties(props);

      // Make sure errors get directed to the right place.
      if (!(trans.getErrorListener() instanceof XTFSaxonErrorListener))
        trans.setErrorListener(new XTFSaxonErrorListener());

      trans.transform(node, tmp);
      return writer.toString();
    }
    catch (Exception e) {
      return "Error writing XML: " + e;
    }
  }
} // class XMLWriter

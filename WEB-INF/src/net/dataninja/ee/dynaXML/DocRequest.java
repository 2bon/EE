package net.dataninja.ee.dynaXML;


/**
net.dataninja copyright statement
 */
import java.util.Vector;
import net.dataninja.ee.textEngine.QueryRequest;

/** Holds document information specific to a docId */
public class DocRequest 
{
  /** Default constructor */
  public DocRequest() {
  }

  /**
   * Copy constructor. Note that the authSpecs vector, while copied,
   * does not copy each authSpec. Rather, the vector contains ref's
   * to the same authSpecs as the original.
   *
   * @param other     DocRequest to copy data from
   */
  public DocRequest(DocRequest other) {
    style = other.style;
    source = other.source;
    indexConfig = other.indexConfig;
    indexName = other.indexName;
    brand = other.brand;
    authSpecs = new Vector(other.authSpecs);
    preFilter = other.preFilter;
    removeDoctypeDecl = other.removeDoctypeDecl;
  }

  /** Path to the display stylesheet (relative to servlet base dir) */
  public String style;

  /** Path to the source XML document (relative to servlet base dir) */
  public String source;

  /** Path to the index configuration file (relative to servlet base dir) */
  public String indexConfig;

  /** Name of the index within which the lazy file is stored */
  public String indexName;

  /**
   * Path to a brand profile (a simple XML document containing
   * parameters that are passed to the display stylesheet. If relative,
   * interpreted relative to the servlet base directory.
   */
  public String brand;

  /**
   * List of authentication specs, which are evaluated in order until one
   * is found that definitely allows or denies access.
   */
  public Vector authSpecs = new Vector(3);

  /** Path to a pre-filter stylesheet to run on the document (or null
   *  for no pre-filtering.)
   */
  public String preFilter;

  /** Whether to remove DOCTYPE declaration from the XML document */
  public boolean removeDoctypeDecl = false;

  /** Text query to run on the document, or null for none. */
  public QueryRequest query;
} // class DocRequest

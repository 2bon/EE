package net.dataninja.ee.dynaXML;


/**
net.dataninja copyright statement
 */

/**
 * This exception is thrown when an attempt is made to apply a text search
 * to a document that isn't present in the index (including a document
 * fetched from a URL.)
 */
class UnsupportedQueryException extends DynaXMLException 
{
  /** Default constructor */
  public UnsupportedQueryException() {
    super("Queries cannot be performed on non-indexed content");
  }
} // class UnsupportedQueryException

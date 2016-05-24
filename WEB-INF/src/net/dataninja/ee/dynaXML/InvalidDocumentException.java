package net.dataninja.ee.dynaXML;


/**
net.dataninja copyright statement
 */

/**
 * This exception is thrown when a requestor attempts to access a document
 * that doesn't exist or the file for it cannot be found.
 */
public class InvalidDocumentException extends DynaXMLException 
{
  /** Default constructor */
  public InvalidDocumentException() {
    super("Invalid document path output from docReqParser stylesheet");
  }
} // class InvalidDocumentException

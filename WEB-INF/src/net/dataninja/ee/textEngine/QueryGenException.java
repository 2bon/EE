package net.dataninja.ee.textEngine;


/**
net.dataninja copyright statement
 */
import net.dataninja.ee.util.GeneralException;

/**
 * Exception class used to report errors from the query parser stylesheet.
 */
class QueryGenException extends GeneralException 
{
  public QueryGenException(String message) {
    super(message);
  }

  public QueryGenException(String message, Throwable cause) {
    super(message, cause);
  }
}

package net.dataninja.ee.crossQuery;


/**
net.dataninja copyright statement
 */
import net.dataninja.ee.util.GeneralException;

/**
 * Exception class used to report errors from the query router stylesheet.
 */
class QueryRouteException extends GeneralException 
{
  public QueryRouteException(String message) {
    super(message);
  }

  public QueryRouteException(String message, Throwable cause) {
    super(message, cause);
  }
}

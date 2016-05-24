package net.dataninja.ee.servletBase;


/**
net.dataninja copyright statement
 */
import net.dataninja.ee.util.GeneralException;

/**
 * This exception is thrown when the request contains an invalid query
 * string.
 */
class CQLParseException extends GeneralException 
{
  /**
   * Constructor that only takes a 'message'.
   *
   * @param message     Message describing cause of this exception.
   */
  public CQLParseException(String message) {
    super(message);
  }

  /** This particular exception isn't really severe enough to log */
  public boolean isSevere() {
    return false;
  }
} // class CQLParseException

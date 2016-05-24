package net.dataninja.ee.servletBase;


/**
net.dataninja copyright statement
 */
import net.dataninja.ee.util.GeneralException;

/**
 * This exception is thrown when the an HTTP redirect has been
 * requested. We use it as a mechanism to pop out of Saxon
 * and abort stylesheet processing.
 */
public class RedirectException extends GeneralException 
{
  /**
   * Constructor that only takes a 'message'.
   *
   * @param message     Message describing cause of this exception.
   */
  public RedirectException(String message) {
    super(message);
  }

  /** This particular exception isn't really severe enough to log */
  public boolean isSevere() {
    return false;
  }
} // class RedirectException

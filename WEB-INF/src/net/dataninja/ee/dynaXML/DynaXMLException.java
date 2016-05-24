package net.dataninja.ee.dynaXML;


/**
net.dataninja copyright statement
 */
import net.dataninja.ee.util.GeneralException;

/**
 * A general-purpose exception used for problems that may occasionally happen
 * and are expected to. When one of these is thrown, the errorGen stylesheet
 * only receives a callstack if isSevere() returns true.
 */
class DynaXMLException extends GeneralException 
{
  /**
   * Default constructor.
   *
   * @param message   Description of what happened
   */
  public DynaXMLException(String message) {
    super(message);
  }

  /**
   * Constructor that includes a reference to the exception that caused
   * this one.
   *
   * @param message   Description of what happened
   * @param cause     The exception that caused this one.
   */
  public DynaXMLException(String message, Throwable cause) {
    super(message, cause);
  }
} // class DynaXMLException

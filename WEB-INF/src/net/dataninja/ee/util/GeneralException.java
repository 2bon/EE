package net.dataninja.ee.util;


/**
net.dataninja copyright statement
 */

/**
 * A general-purpose exception used for problems that may occasionally happen
 * and are expected to. When one of these is thrown, the errorGen stylesheet
 * only receives a callstack if isSevere() returns true.
 */
public class GeneralException extends RuntimeException 
{
  /**
   * Default constructor.
   *
   * @param message   Description of what happened
   */
  public GeneralException(String message) {
    super(message);
  }

  /**
   * Constructor that includes a reference to the exception that caused
   * this one.
   *
   * @param message   Description of what happened
   * @param cause     The exception that caused this one.
   */
  public GeneralException(String message, Throwable cause) {
    super(message, cause);
  }

  /** Sets an attribute for further information on the exception.  */
  public void set(String attribName, String attribValue) {
    attribs.put(attribName, attribValue);
  }

  /**
   * Tells whether this is a really bad problem. Derived classes should
   * override if it's not. If this method returns true, a call stack is
   * passed to the errorGen stylesheet.
   */
  public boolean isSevere() {
    return true;
  }

  /** Attributes that give more info on the exception */
  public AttribList attribs = new AttribList();
} // class GeneralException

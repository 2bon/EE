package net.dataninja.ee.dynaXML;


/**
net.dataninja copyright statement
 */

/**
 * This exception is thrown when a requestor fails authentication (ie has
 * the wrong password, IP address, etc).
 */
class NoPermissionException extends DynaXMLException 
{
  /**
   * Constructor taking an IP address
   *
   * @param ipAddr    The IP address of the requestor
   */
  public NoPermissionException(String ipAddr) {
    super("Permission denied");
    set("ipAddr", ipAddr);
  }

  /**
   * Constructor that only takes a 'cause'. This is used, for example, when
   * an LDAP authentication attempt fails due to a communication error.
   *
   * @param cause     The exception that caused this exception.
   */
  public NoPermissionException(Throwable cause) {
    super("Permission denied", cause);
  }

  /** Default constructor */
  public NoPermissionException() {
    super("Permission denied");
  }

  /** This particular exception isn't really severe enough to log */
  public boolean isSevere() {
    return false;
  }
} // class NoPermissionException

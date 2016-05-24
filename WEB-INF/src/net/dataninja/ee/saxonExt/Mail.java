package net.dataninja.ee.saxonExt;


/*
dataninja copyright statement
 */

import net.dataninja.ee.saxonExt.mail.SendElement;
import net.sf.saxon.style.ExtensionElementFactory;

/**
 * Front-end to the "Mail" Saxon extension, which allows stylesheets to send
 * email using an SMTP server.
 *
 * @author Rick Li
 */
public class Mail implements ExtensionElementFactory 
{
  /**
  * Identify the class to be used for stylesheet elements with a given local name.
  * The returned class must extend net.sf.saxon.style.StyleElement
  * @return null if the local name is not a recognised element type in this
  * namespace.
  */
  public Class getExtensionClass(String localname) 
  {
    if (localname.equals("send"))
      return SendElement.class;

    return null;
  }
}

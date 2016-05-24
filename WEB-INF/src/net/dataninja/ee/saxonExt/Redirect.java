package net.dataninja.ee.saxonExt;


/*
dataninja copyright statement
 *
 * Acknowledgements:
 *
 * A significant amount of new and/or modified code in this module
 * was made possible by a grant from the Andrew W. Mellon Foundation,
 * as part of the Melvyl Recommender Project.
 */
import net.dataninja.ee.saxonExt.redirect.HttpErrorElement;
import net.dataninja.ee.saxonExt.redirect.RedirectElement;

import net.sf.saxon.style.ExtensionElementFactory;

/**
 * Implements the "Redirect" Saxon extension, which allows stylesheets to
 * force an immediate HTTP redirect to a different URL.
 *
 * This extension should be used prior to generating any output; otherwise
 * an exception will be thrown.
 *
 * @author Rick Li
 */
public class Redirect implements ExtensionElementFactory 
{
  /**
  * Identify the class to be used for stylesheet elements with a given local name.
  * The returned class must extend net.sf.saxon.style.StyleElement
  * @return null if the local name is not a recognised element type in this
  * namespace.
  */
  public Class getExtensionClass(String localname) 
  {
    if (localname.equals("send") || localname.equalsIgnoreCase("sendRedirect"))
      return RedirectElement.class;
    if (localname.equalsIgnoreCase("sendHttpError"))
      return HttpErrorElement.class;

    return null;
  }
} // class Redirect

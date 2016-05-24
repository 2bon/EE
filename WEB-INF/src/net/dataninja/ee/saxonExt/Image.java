package net.dataninja.ee.saxonExt;


/*
dataninja copyright statement
 */

import net.dataninja.ee.saxonExt.image.OutputElement;
import net.sf.saxon.style.ExtensionElementFactory;

/**
 * Front-end to the "Image" Saxon extension, which allows stylesheets to perform
 * image extraction and highlighting.
 *
 * @author Rick Li
 */
public class Image implements ExtensionElementFactory 
{
  /**
  * Identify the class to be used for stylesheet elements with a given local name.
  * The returned class must extend net.sf.saxon.style.StyleElement
  * @return null if the local name is not a recognised element type in this
  * namespace.
  */
  public Class getExtensionClass(String localname) 
  {
    if (localname.equals("output"))
      return OutputElement.class;

    return null;
  }
}

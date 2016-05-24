package net.dataninja.ee.saxonExt;


/*
net.dataninja copyright statement
 *
 * Acknowledgements:
 *
 * A significant amount of new and/or modified code in this module
 * was made possible by a grant from the Andrew W. Mellon Foundation,
 * as part of the Melvyl Recommender Project.
 */
import net.dataninja.ee.saxonExt.exec.ArgElement;
import net.dataninja.ee.saxonExt.exec.InputElement;
import net.dataninja.ee.saxonExt.exec.PipeImageElement;
import net.dataninja.ee.saxonExt.exec.RunElement;

import net.sf.saxon.style.ExtensionElementFactory;

/**
 * Front-end to the "Exec" Saxon extension, which allows stylesheets to call
 * command-line programs, with proper error handling, timeouts, and format
 * conversion.
 *
 * @author Rick Li
 */
public class Exec implements ExtensionElementFactory 
{
  /**
  * Identify the class to be used for stylesheet elements with a given local name.
  * The returned class must extend net.sf.saxon.style.StyleElement
  * @return null if the local name is not a recognised element type in this
  * namespace.
  */
  public Class getExtensionClass(String localname) 
  {
    if (localname.equals("run"))
      return RunElement.class;
    
    if (localname.equals("pipeImage"))
      return PipeImageElement.class;

    if (localname.equals("arg") || localname.equals("argument"))
      return ArgElement.class;

    if (localname.equals("input"))
      return InputElement.class;

    return null;
  }
}

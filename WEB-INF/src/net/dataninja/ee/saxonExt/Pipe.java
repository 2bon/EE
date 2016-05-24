package net.dataninja.ee.saxonExt;


/*
dataninja copyright statement
 */
import net.dataninja.ee.saxonExt.pipe.PipeFileElement;
import net.dataninja.ee.saxonExt.pipe.PipeFopElement;
import net.dataninja.ee.saxonExt.pipe.PipeRequestElement;

import net.sf.saxon.style.ExtensionElementFactory;

/**
 * Front-end to the "Pipe" Saxon extension, which allows stylesheets to pipe
 * files, or results of external HTTP requests, directly as the stylesheet's
 * HTTP response.
 *
 * @author Rick Li
 */
public class Pipe implements ExtensionElementFactory 
{
  /**
  * Identify the class to be used for stylesheet elements with a given local name.
  * The returned class must extend net.sf.saxon.style.StyleElement
  * @return null if the local name is not a recognised element type in this
  * namespace.
  */
  public Class getExtensionClass(String localname) 
  {
    if (localname.equals("pipeRequest"))
      return PipeRequestElement.class;

    if (localname.equals("pipeFile"))
      return PipeFileElement.class;

    if (localname.equals("pipeFOP"))
      return PipeFopElement.class;

    return null;
  }
}

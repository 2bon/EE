package net.dataninja.ee.saxonExt.exec;

import net.sf.saxon.expr.Expression;
import net.sf.saxon.instruct.Executable;
import net.sf.saxon.trans.XPathException;

/*
dataninja copyright statement
 */

/**
 * Implements almost the same thing as Exec.run saxon extension, except that
 * instead of returning stdout as a string or XML, it is interpreted as
 * an image and sent directly to the servlet output stream (with a proper
 * MIME type). Images of type PNG and JPEG are acceptable; others will
 * cause an exception to be thrown.
 */
public class PipeImageElement extends RunElement 
{
  public Expression compile(Executable exec)
    throws XPathException 
  {
    return new PipeImageInstruction(command, timeoutMsec, getArgInstructions(exec));
  }
} // class PipeImageElement

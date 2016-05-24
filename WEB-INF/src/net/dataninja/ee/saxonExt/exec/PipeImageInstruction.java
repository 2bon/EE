package net.dataninja.ee.saxonExt.exec;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.dataninja.ee.servletBase.TextServlet;

import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
import net.sf.saxon.trans.XPathException;

/*
dataninja copyright statement
 */

/**
 * Helper class that does most of the work for {@link PipeImageElement}.
 */
public class PipeImageInstruction extends RunInstruction 
{
  public PipeImageInstruction(Expression command, int timeout, List args)
  {
    super(command, timeout, args);
  }
  
  public String getExpressionType() {
    return "exec:pipeImage";
  }

  public Item evaluateItem(XPathContext context)
    throws XPathException 
  {
    String[] argArray = gatherArgs(context);
    byte[] outBytes = runAndGrab(context, argArray);
    
    // Figure out whether the returned data is PNG or JPEG data
    int[] pngHeader  = { 0x89, 0x50, 0x4e, 0x47 };
    int[] jpegHeader = { 0xff, 0xd8, 0xff, 0xe0 };
    boolean isPNG  = true;
    boolean isJPEG = true;
    for (int i = 0; i < 4; i++) {
      if (i >= outBytes.length || outBytes[i] != (byte)pngHeader[i])
        isPNG = false;
      if (i >= outBytes.length || outBytes[i] != (byte)jpegHeader[i])
        isJPEG = false;
    }

    // It better be one or the other
    if (!isPNG && !isJPEG) {
      dynamicError(
          "Error: no PNG or JPEG returned by external command '" + command,
          "IMPI0001", context);
    }
    
    // Set the corresponding MIME type
    HttpServletResponse res = TextServlet.getCurResponse();
    if (isPNG)
      res.setContentType("image/png");
    else
      res.setContentType("image/jpeg");
    
    // Finally, output the result.
    ServletOutputStream out;
    try {
      out = res.getOutputStream();
      out.write(outBytes);
    } catch (IOException e) {
      dynamicError("Exception while writing output stream", "IMPI0002", context);
    }
    return null;
  } // evaluateItem()

} // class PipeImageInstruction

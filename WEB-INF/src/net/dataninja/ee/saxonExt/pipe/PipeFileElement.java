package net.dataninja.ee.saxonExt.pipe;

/*
dataninja copyright statement
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.dataninja.ee.saxonExt.ElementWithContent;
import net.dataninja.ee.saxonExt.InstructionWithContent;
import net.dataninja.ee.servletBase.TextServlet;
import net.dataninja.ee.xslt.FileUtils;

import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.instruct.Executable;
import net.sf.saxon.instruct.TailCall;
import net.sf.saxon.trans.XPathException;

/**
 * Pipes the contents of a file directly to the servlet request
 * output stream, bypassing any further stylesheet processing.
 */
public class PipeFileElement extends ElementWithContent 
{
  public void prepareAttributes() throws XPathException 
  {
    String[] mandatoryAtts = { "path", "mimeType" };
    String[] optionalAtts = { "fileName" };
    parseAttributes(mandatoryAtts, optionalAtts);
  }

  public Expression compile(Executable exec) throws XPathException { 
    return new PipeFileInstruction(attribs, compileContent(exec));
  }

  /** Worker class for PipeFileElement */
  private static class PipeFileInstruction extends InstructionWithContent 
  {
    public PipeFileInstruction(Map<String, Expression> attribs, Expression content) 
    {
      super("pipe:pipeFile", attribs, content);
    }

    /**
     * The real workhorse.
     */
    @Override
    public TailCall processLeavingTail(XPathContext context) 
      throws XPathException 
    {
      // Build the full path.
      String path = attribs.get("path").evaluateAsString(context);
      File file = FileUtils.resolveFile(context, path);
      
      // Make sure it's readable.
      if (!file.canRead()) {
        dynamicError("Cannot read path '" + path + "' (resolved to '" + file.toString() + "'", 
                     "PIPE_FILE_001", context);
      }
      
      // Set the content length and type
      HttpServletResponse servletResponse = TextServlet.getCurResponse();
      servletResponse.setHeader("Content-length", Long.toString(file.length()));
      servletResponse.setHeader("Content-type", attribs.get("mimeType").evaluateAsString(context));
      
      // If file name specified, add the Content-disposition header.
      String fileName;
      if (attribs.containsKey("fileName")) {
        fileName = attribs.get("fileName").evaluateAsString(context);
        servletResponse.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "\"");
      }
      
      // Now copy the file to the output stream.
      try {
        copyFileToStream(file, servletResponse.getOutputStream());
      }
      catch (IOException e) {
        dynamicError("IO Error while piping file: " + e.toString(), "PIPE_FILE_002", context);
      }
          
      // All done.
      return null;
    }
  }
  
  /** Utility method to copy the contents of a file into an output stream */
  public static void copyFileToStream(File inFilePath, OutputStream outStream) 
    throws IOException
  {
    InputStream fileIn = null;
    try
    {
      fileIn = new FileInputStream(inFilePath);
      copyStreamToStream(fileIn, outStream);
    } 
    finally 
    {
      // Clean up after ourselves.
      if (fileIn != null)
        try { fileIn.close(); } catch (IOException e) { /* ignore */ }
    }
  }
  
  /** Utility method to copy the entire contents of an input stream into an output stream */
  public static void copyStreamToStream(InputStream fileIn, OutputStream outStream) 
    throws IOException
  {
    byte[] buf = null;
    try
    {
      buf = PipeBufferPool.allocBuffer();
      int got;
      while ((got = fileIn.read(buf)) >= 0)
        outStream.write(buf, 0, got);
      outStream.flush();
    } 
    finally 
    {
      // Clean up after ourselves.
      if (buf != null)
        PipeBufferPool.deallocBuffer(buf);
    }
  }
}

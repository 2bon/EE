package net.dataninja.ee.saxonExt.redirect;

/*
dataninja copyright statement
 */

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.SimpleExpression;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.instruct.Executable;
import net.sf.saxon.om.Item;
import net.sf.saxon.style.ExtensionInstruction;
import net.sf.saxon.trans.DynamicError;
import net.sf.saxon.trans.XPathException;

import net.dataninja.ee.servletBase.TextServlet;

/**
 * Implements a Saxon instruction that generates an HTTP error
 * with a code and optional message.
 *
 * @author Rick Li
 */
public class HttpErrorElement extends ExtensionInstruction 
{
  Expression codeExp;
  Expression messageExp;

  public void prepareAttributes()
    throws XPathException 
  {
    // Get mandatory 'code' attribute
    String codeAtt = getAttributeList().getValue("", "code");
    if (codeAtt == null) {
      reportAbsence("code");
      return;
    }
    codeExp = makeAttributeValueTemplate(codeAtt);
    
    // Get optional 'message' attribute
    String messageAtt = getAttributeList().getValue("", "message");
    if (messageAtt != null)
      messageExp = makeAttributeValueTemplate(messageAtt);
    
  } // prepareAttributes()

  public Expression compile(Executable exec)
    throws XPathException 
  {
    return new HttpErrorInstruction(codeExp, messageExp);
  }

  private class HttpErrorInstruction extends SimpleExpression 
  {
    Expression codeExp;
    Expression messageExp;

    public HttpErrorInstruction(Expression codeExp, Expression messageExp) {
      this.codeExp = codeExp;
      this.messageExp = messageExp;
    }

    /**
     * A subclass must provide one of the methods evaluateItem(), iterate(), or process().
     * This method indicates which of the three is provided.
     */
    public int getImplementationMethod() {
      return Expression.EVALUATE_METHOD;
    }

    public String getExpressionType() {
      return "redirect:sendHttpError";
    }

    public Item evaluateItem(XPathContext context)
      throws XPathException 
    {
      HttpServletResponse res = TextServlet.getCurResponse();
      
      try 
      {
        // Determine the code to send
        String codeStr = codeExp.evaluateAsString(context);
        int code = Integer.parseInt(codeStr);
        
        // Send it, with message if specified.
        if (messageExp == null)
          res.sendError(code);
        else
          res.sendError(code, messageExp.evaluateAsString(context));
      }
      catch (IOException e) {
        throw new DynamicError(e);
      }
      catch (NumberFormatException e) {
        throw new DynamicError(e);
      }
      return null;
    }
  }
}
package net.dataninja.ee.saxonExt.redirect;

/*
dataninja copyright statement
 *
 * Acknowledgements:
 *
 * A significant amount of new and/or modified code in this module
 * was made possible by a grant from the Andrew W. Mellon Foundation,
 * as part of the Melvyl Recommender Project.
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
 * Implements a Saxon instruction that generates an HTTP redirect
 * to a different URL.
 *
 * @author Rick Li
 */
public class RedirectElement extends ExtensionInstruction 
{
  Expression urlExp;
  Expression permanentExp;

  public void prepareAttributes()
    throws XPathException 
  {
    // Get mandatory 'url' attribute
    String urlAtt = getAttributeList().getValue("", "url");
    if (urlAtt == null) {
      reportAbsence("url");
      return;
    }
    urlExp = makeAttributeValueTemplate(urlAtt);
    
    // Get optional 'permanent' attribute
    String permAtt = getAttributeList().getValue("", "permanent");
    if (permAtt != null)
      permanentExp = makeAttributeValueTemplate(permAtt);
  } // prepareAttributes()

  public Expression compile(Executable exec)
    throws XPathException 
  {
    return new RedirectInstruction(urlExp);
  }

  private class RedirectInstruction extends SimpleExpression 
  {
    Expression urlExp;

    public RedirectInstruction(Expression urlExp) {
      this.urlExp = urlExp;
    }

    /**
     * A subclass must provide one of the methods evaluateItem(), iterate(), or process().
     * This method indicates which of the three is provided.
     */
    public int getImplementationMethod() {
      return Expression.EVALUATE_METHOD;
    }

    public String getExpressionType() {
      return "redirect:sendRedirect";
    }

    public Item evaluateItem(XPathContext context)
      throws XPathException 
    {
      HttpServletResponse res = TextServlet.getCurResponse();
      String url = urlExp.evaluateAsString(context);
      String encodedUrl;
      // Tomcat, starting around ver 6.0.21, started adding jsessionid everywhere. Stop that!
      if (TextServlet.getCurServlet().getConfig().sessionEncodeURLPattern == null)
        encodedUrl = url;
      else
        encodedUrl = res.encodeRedirectURL(url);
      String permanentStr = "no";
      if (permanentExp != null)
        permanentStr = permanentExp.evaluateAsString(context);
      
      try {
        if (permanentStr.matches("yes|Yes|true|True|1")) {
          res.setHeader("Location", encodedUrl);
          res.sendError(301);           // HTTP 301 Moved Permanently
        }
        else
          res.sendRedirect(encodedUrl); // HTTP 302 Moved Temporarily
      }
      catch (IOException e) {
        throw new DynamicError(e);
      }
      return null;
    }
  }
}
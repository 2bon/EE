package net.dataninja.ee.saxonExt.mail;


/*
dataninja copyright statement
 */

import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import net.dataninja.ee.saxonExt.ElementWithContent;
import net.dataninja.ee.saxonExt.InstructionWithContent;

import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.instruct.Executable;
import net.sf.saxon.instruct.TailCall;
import net.sf.saxon.trans.DynamicError;
import net.sf.saxon.trans.XPathException;

/**
 * Implements a Saxon instruction that reads an image from the filesystem,
 * optionally modifies it in various ways, and outputs it directly via the
 * current HttpServletResponse.
 *
 * @author Rick Li
 */
public class SendElement extends ElementWithContent 
{
  public void prepareAttributes()
    throws XPathException 
  {
    String[] mandatoryAtts = { "smtpHost", "from", "to", "subject" };
    String[] optionalAtts = { "smtpUser", "smtpPassword", "useSSL" };
    parseAttributes(mandatoryAtts, optionalAtts);
  }

  public Expression compile(Executable exec) throws XPathException { 
    return new SendInstruction(attribs, compileContent(exec));
  }

  private static class SendInstruction extends InstructionWithContent 
  {
    public SendInstruction(Map<String, Expression> attribs, Expression content) 
    {
      super("mail:send", attribs, content);
    }

    public TailCall processLeavingTail(final XPathContext context) throws XPathException 
    {
      boolean debug = false;
      
      try 
      {
        // Determine the proper protocol
        String protocol = "";
        String useSSL = attribs.get("useSSL").evaluateAsString(context);
        if (useSSL != null && useSSL.matches("^(yes|Yes|true|True|1)$"))
          protocol = "smtps";
        else
          protocol = "smtp";
        
        // If user name and password were specified, do authentication.
        boolean doAuth = attribs.containsKey("smtpUser") && attribs.containsKey("smtpPassword");
        
        // Set up SMTP host and authentication information
        Properties props = new Properties();
        String server = attribs.get("smtpHost").evaluateAsString(context); 
        props.put("mail." + protocol + ".host", server);
        props.put("mail." + protocol + ".auth", doAuth ? "true" : "false");
        
        // Create the mail session, with authentication if appropriate.
        Session session;
        if (doAuth)
        {
          Authenticator auth = new Authenticator() 
          {
            public PasswordAuthentication getPasswordAuthentication() 
            {
              try {
                String username = attribs.get("smtpUser").evaluateAsString(context);
                String password = attribs.get("smtpPassword").evaluateAsString(context);
                return new PasswordAuthentication(username, password);
              }
              catch (XPathException e) {
                throw new RuntimeException(e);
              }
            }
          };
          session = Session.getInstance(props, auth);
        }
        else
          session = Session.getInstance(props);
        
        // Set the debug mode on the session.
        session.setDebug(debug);
        
        // Create a message and specify who it's from
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(attribs.get("from").evaluateAsString(context)));
        
        // Parse a list of recipients, separated by commas.
        String recips = attribs.get("to").evaluateAsString(context).trim();
        ArrayList<InternetAddress> addressTo = new ArrayList<InternetAddress>();
        for (String recip : recips.split(",")) {
          recip = recip.trim();
          if (recip.length() > 0)
            addressTo.add(new InternetAddress(recip));
        }
        
        // Add the recipient list to the message
        msg.setRecipients(Message.RecipientType.TO, 
                          addressTo.toArray(new InternetAddress[addressTo.size()]));
        
        // Set the subject
        msg.setSubject(attribs.get("subject").evaluateAsString(context));
        
        // Convert the content to a string (we can't use evaluateAsString() since
        // it only pays attention to the first item in a sequence.)
        //
        msg.setText(sequenceToString(content, context), "UTF-8");
        msg.saveChanges();
        
        // Finally, send the message.
        Transport tr = session.getTransport(protocol);
        tr.connect();
        tr.sendMessage(msg, msg.getAllRecipients());
        try {
          tr.close();
        }
        catch (MessagingException e) {
          // ignore errors during close()
        }
      }
      catch (MessagingException e) {
        throw new DynamicError(e);
      }
      catch (XPathException e) {
        throw e;
      }
 
      return null;
    }

  }
} // class SendElement

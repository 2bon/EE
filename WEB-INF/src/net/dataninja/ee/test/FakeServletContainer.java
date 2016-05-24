package net.dataninja.ee.test;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import net.dataninja.ee.crossQuery.CrossQuery;
import net.dataninja.ee.dynaXML.DynaXML;
import net.dataninja.ee.util.Trace;

//
// See license at end of file.
 
/**
 * Allows command-line access to crossQuery and dynaXML by simulating a servlet
 * container.
 */
public class FakeServletContainer 
{
  private static CrossQuery crossQuery;
  private static DynaXML dynaXML;
  private static FakeServletConfig config;

  private static void service(HttpServlet servlet, String url) 
    throws ServletException, IOException
  {
    FakeServletRequest req = new FakeServletRequest(url);
    FakeOutputStream out = new FakeOutputStream();
    FakeServletResponse res = new FakeServletResponse(out);
    servlet.service(req, res);
    System.out.print(out.buf.toString());
  }
  
  private static void service(String url) 
    throws ServletException, IOException, InterruptedException
  {
    Pattern pausePat = Pattern.compile("pause\\((\\d+)\\)");
    Matcher m = pausePat.matcher(url);
    if (m.matches()) {
      int secs = Integer.parseInt(m.group(1));
      Trace.info(String.format("Pausing %s seconds", secs));
      Thread.sleep(secs);
    }
    else if (url.contains("/search")) {
      if (crossQuery == null) {
        crossQuery = new CrossQuery();
        crossQuery.init(config);
      }
      Trace.info("Servicing crossQuery URL '" + url + "'");
      service(crossQuery, url);
    }
    else if (url.contains("/view")) {
      if (dynaXML == null) {
        dynaXML = new DynaXML();
        dynaXML.init(config);
      }
      Trace.info("Servicing dynaXML URL '" + url + "'");
      service(dynaXML, url);
    }
    else
      Trace.warning("Unrecognized URL pattern: '" + url + "'");
  }
  
  public static void main(String[] args) 
    throws InterruptedException, ServletException, IOException
  {
    FakeServletContext context = new FakeServletContext();
    config = new FakeServletConfig(context, System.getProperty("user.dir"), "crossQuery");

    for (int i=0; i<args.length; i++)
      service(args[i]);
  }
}

/**
dataninja copyright statement
 */
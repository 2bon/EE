package net.dataninja.ee.test;


/**
net.dataninja copyright statement
 */
import java.util.Enumeration;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * Used to abuse dynaXML and crossQuery, providing them only as much context
 * as they need in the testing environment.
 *
 * @author Rick Li
 */
public class FakeServletConfig implements ServletConfig 
{
  private FakeServletContext context;
  private String baseDir;
  private String servletName;

  public FakeServletConfig(FakeServletContext context, String baseDir,
                           String servletName) 
  {
    this.context = context;
    this.baseDir = baseDir;
    this.servletName = servletName;
  }

  public String getInitParameter(String name) {
    if (name.equals("base-dir"))
      return baseDir;
    assert false;
    return null;
  }

  public Enumeration getInitParameterNames() {
    assert false;
    return null;
  }

  public ServletContext getServletContext() {
    return context;
  }

  public String getServletName() {
    return servletName;
  }
} // class FakeServletConfig

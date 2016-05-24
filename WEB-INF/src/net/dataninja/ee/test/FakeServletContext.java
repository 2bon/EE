package net.dataninja.ee.test;


/**
net.dataninja copyright statement
 */
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import net.dataninja.ee.util.Path;

/**
 * Used to abuse dynaXML and crossQuery, providing only as much context as
 * they need within the test environment.
 *
 * @author Rick Li
 */
public class FakeServletContext implements ServletContext 
{
  public Object getAttribute(String name) {
    assert false;
    return null;
  }

  public Enumeration getAttributeNames() {
    assert false;
    return null;
  }

  public ServletContext getContext(String uripath) {
    assert false;
    return null;
  }

  public String getInitParameter(String name) {
    assert false;
    return null;
  }

  public Enumeration getInitParameterNames() {
    assert false;
    return null;
  }

  public int getMajorVersion() {
    assert false;
    return 0;
  }

  public String getMimeType(String file) {
    assert false;
    return null;
  }

  public int getMinorVersion() {
    assert false;
    return 0;
  }

  public RequestDispatcher getNamedDispatcher(String name) {
    assert false;
    return null;
  }

  public String getRealPath(String path) {
    String homeDir = System.getProperty("ee.home");
    return Path.resolveRelOrAbs(homeDir, path);
  }

  public RequestDispatcher getRequestDispatcher(String path) {
    assert false;
    return null;
  }

  public URL getResource(String path)
    throws MalformedURLException 
  {
    assert false;
    return null;
  }

  public InputStream getResourceAsStream(String path) {
    assert false;
    return null;
  }

  public Set getResourcePaths(String path) {
    assert false;
    return null;
  }

  public String getServerInfo() {
    assert false;
    return null;
  }

  public Servlet getServlet(String name) {
    assert false;
    return null;
  }

  public String getServletContextName() {
    assert false;
    return null;
  }

  public Enumeration getServletNames() {
    assert false;
    return null;
  }

  public Enumeration getServlets() {
    assert false;
    return null;
  }

  public void log(Exception exception, String msg) {
    assert false;
  }

  public void log(String message, Throwable throwable) {
    assert false;
  }

  public void log(String msg) {
    return;
  }

  public void removeAttribute(String name) {
    assert false;
  }

  public void setAttribute(String name, Object object) {
    assert false;
  }

  public String getContextPath() {
    return null;
  }
} // class FakeServletContext

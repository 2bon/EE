package net.dataninja.ee.test;


/**
net.dataninja copyright statement
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Used to gather the response from crossQuery or dynaXML during a test.
 *
 * @author Rick Li
 */
public class FakeServletResponse implements HttpServletResponse 
{
  ServletOutputStream out;

  public FakeServletResponse(ServletOutputStream out) {
    this.out = out;
  }

  public void addCookie(Cookie cookie) {
    assert false;
  }

  public String getContentType() {
    assert false;
    return null;
  }

  public void setCharacterEncoding(String arg0) {
    assert false;
  }

  public void addDateHeader(String name, long date) {
    return;
  }

  public void addHeader(String name, String value) {
    return;
  }

  public void addIntHeader(String name, int value) {
    return;
  }

  public boolean containsHeader(String name) {
    assert false;
    return false;
  }

  public String encodeRedirectUrl(String url) {
    return url;
  }

  public String encodeRedirectURL(String url) {
    return url;
  }

  public String encodeUrl(String url) {
    return url;
  }

  public String encodeURL(String url) {
    return url;
  }

  public void sendError(int sc, String msg)
    throws IOException 
  {
    assert false;
  }

  public void sendError(int sc)
    throws IOException 
  {
    assert false;
  }

  public void sendRedirect(String location)
    throws IOException 
  {
    assert false;
  }

  public void setDateHeader(String name, long date) {
    return;
  }

  public void setHeader(String name, String value) {
    return;
  }

  public void setIntHeader(String name, int value) {
    return;
  }

  public void setStatus(int sc, String sm) {
    assert false;
  }

  public void setStatus(int sc) {
    assert false;
  }

  public void flushBuffer()
    throws IOException 
  {
    assert false;
  }

  public int getBufferSize() {
    assert false;
    return 0;
  }

  public String getCharacterEncoding() {
    assert false;
    return null;
  }

  public Locale getLocale() {
    assert false;
    return null;
  }

  public ServletOutputStream getOutputStream()
    throws IOException 
  {
    return out;
  }

  public PrintWriter getWriter()
    throws IOException 
  {
    assert false;
    return null;
  }

  public boolean isCommitted() {
    assert false;
    return false;
  }

  public void reset()
    throws IllegalStateException 
  {
    assert false;
  }

  public void resetBuffer()
    throws IllegalStateException 
  {
    assert false;
  }

  public void setBufferSize(int size) {
    assert false;
  }

  public void setContentLength(int len) {
    assert false;
  }

  public void setContentType(String type) 
  {
    // Do nothing.
  }

  public void setLocale(Locale loc) {
    assert false;
  }
}

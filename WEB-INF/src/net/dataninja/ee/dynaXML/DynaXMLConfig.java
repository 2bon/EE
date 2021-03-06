package net.dataninja.ee.dynaXML;


/**
net.dataninja copyright statement
 */
import net.dataninja.ee.servletBase.TextConfig;
import net.dataninja.ee.util.GeneralException;

/** Holds global configuration information for the dynaXML servlet.  */
class DynaXMLConfig extends TextConfig 
{
  /** IP address of the reverse proxy, if any */
  public String reverseProxyIP;

  /**
   * Name of the special HTTP header used to record the original IP
   * address by the reverse proxy.
   */
  public String reverseProxyMarker;

  /** The default header to recording the original IP address. */
  public String reverseProxyDefaultMarker = "X-Forwarded-For";

  /**
   * Filesystem path to the 'doclookup' stylesheet, used to get info
   * about documents given their docId's.
   */
  public String docLookupSheet;

  /** Max # of authentication lookups to cache */
  public int authCacheSize = 1000;

  /** Max amount of time (seconds) to cache authentication lookups */
  public int authCacheExpire = 30 * 60; // 30 minutes

  /** Max # of simultaneous external logins */
  public int loginCacheSize = 100;

  /** Max amount of time (seconds) before login attempt fails */
  public int loginCacheExpire = 5 * 60; // 5 minutes

  /** Max # of IP lists to cache */
  public int ipListCacheSize = 10;

  /** Max amount of time (seconds) before IP list is automatically reloaded */
  public int ipListCacheExpire = 15 * 60; // 15 minutes

  /** Whether to use lazy files */
  public boolean useLazyFiles = true;
  
  /** Whether to generate lazy files alone (outside of textIndexer) */
  public boolean buildLazyFilesAlone = false;

  /**
   * Constructor - Reads and parses the global configuration file (XML) for
   * the servlet.
   *
   * @param  path                Filesystem path to the config file.
   * @throws DynaXMLException    If a read or parse error occurs.
   */
  public DynaXMLConfig(DynaXML servlet, String path)
    throws GeneralException 
  {
    super(servlet);
    super.read("dynaXML-config", path);

    // Make sure required things were specified.
    requireOrElse(docLookupSheet,
                  "Config file error: docReqParser path not specified");
  }

  /**
   * Called by when a property is encountered in the configuration file.
   * If we recognize the property we process it here; otherwise, we pass
   * it on to the base class for recognition there.
   */
  public boolean handleProperty(String tagAttr, String strVal) 
  {
    if (tagAttr.equalsIgnoreCase("reverseProxy.IP")) {
      reverseProxyIP = strVal;
      return true;
    }
    else if (tagAttr.equalsIgnoreCase("reverseProxy.marker")) {
      reverseProxyMarker = strVal;
      return true;
    }
    else if (tagAttr.equalsIgnoreCase("reqParserCache.size") ||
             tagAttr.equalsIgnoreCase("reqParserCache.expire") ||
             tagAttr.equalsIgnoreCase("docReqParser.params")) 
    {
      // Obsolete but accepted for backward compatibility
      return true;
    }
    else if (tagAttr.equalsIgnoreCase("docReqParser.path")) {
      docLookupSheet = servlet.getRealPath(strVal);
      return true;
    }
    else if (tagAttr.equalsIgnoreCase("ipListCache.size")) {
      ipListCacheSize = parseInt(tagAttr, strVal);
      return true;
    }
    else if (tagAttr.equalsIgnoreCase("ipListCache.expire")) {
      ipListCacheExpire = parseInt(tagAttr, strVal);
      return true;
    }
    else if (tagAttr.equalsIgnoreCase("authCache.size")) {
      authCacheSize = parseInt(tagAttr, strVal);
      return true;
    }
    else if (tagAttr.equalsIgnoreCase("authCache.expire")) {
      authCacheExpire = parseInt(tagAttr, strVal);
      return true;
    }
    else if (tagAttr.equalsIgnoreCase("loginCache.size")) {
      loginCacheSize = parseInt(tagAttr, strVal);
      return true;
    }
    else if (tagAttr.equalsIgnoreCase("loginCache.expire")) {
      loginCacheExpire = parseInt(tagAttr, strVal);
      return true;
    }
    else if (tagAttr.equalsIgnoreCase("lazyTrees.use")) {
      useLazyFiles = parseBoolean(tagAttr, strVal);
      return true;
    }
    else if (tagAttr.equalsIgnoreCase("lazyTrees.buildAlone")) {
      buildLazyFilesAlone = parseBoolean(tagAttr, strVal);
      return true;
    }

    // Don't recognize it... see if the base class does.
    return super.handleProperty(tagAttr, strVal);
  } // handleProperty()
} // class DynaXMLConfig

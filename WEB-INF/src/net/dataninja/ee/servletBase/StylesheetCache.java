package net.dataninja.ee.servletBase;


/**
net.dataninja copyright statement
 */
import java.io.File;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;

import net.sf.saxon.Configuration;
import net.sf.saxon.FeatureKeys;
import net.sf.saxon.om.NamePool;
import net.sf.saxon.trace.TraceListener;

import org.xml.sax.InputSource;
import net.dataninja.ee.cache.FileDependency;
import net.dataninja.ee.cache.GeneratingCache;
import net.dataninja.ee.util.*;

/**
 * This class is used to cache stylesheets so they don't have to be
 * reloaded each time they're used.
 */
public class StylesheetCache extends GeneratingCache 
{
  private boolean dependencyChecking = false;
  private GeneratingCache dependencyReceiver = null;
  private TraceListenerFactory traceListenerFactory = null;
  private TransformerFactory factory;
  
  public interface TraceListenerFactory {
    TraceListener createListener();
  }
  
  /**
   * Constructor.
   *
   * @param maxEntries    Max # of entries before old ones are flushed
   * @param maxTime       Max age (in seconds) before an entry is flushed.
   * @param dependencyChecking Whether to keep track of dependencies and
   *                           invalidate cache entries when dependents
   *                           are updated.
   */
  public StylesheetCache(int maxEntries, int maxTime, boolean dependencyChecking) {
    super(maxEntries, maxTime);
    this.dependencyChecking = dependencyChecking;
    
    // Create a Saxon Configuration, and make it use the default name pool
    // (it defaults to making a new name pool).
    //
    Configuration config = new Configuration();
    config.setNamePool(NamePool.getDefaultNamePool());

    // Make a factory that will compile stylesheets for us. Force it to use
    // our centralized Configuration.
    //
    factory = new net.sf.saxon.TransformerFactoryImpl(config);
    
    // We want to report errors in a nice, servlet kind of way.
    if (!(factory.getErrorListener() instanceof XTFSaxonErrorListener))
      factory.setErrorListener(new XTFSaxonErrorListener());
    
    // Avoid loading external DTDs if possible. This not only speeds
    // things up, but allows our service to work without depending on
    // external servers being up and running at every moment.
    //
    factory.setAttribute(FeatureKeys.SOURCE_PARSER_CLASS,
                         DTDSuppressingXMLReader.class.getName());

    // Set a URI resolver for dependency checking, if enabled.
    if (dependencyChecking)
      factory.setURIResolver(new DepResolver(this, factory.getURIResolver()));
  }

  /**
   * Locate the stylesheet for the given filesystem path. If not cached,
   * then load it.
   *
   * @param  path         Filesystem path of the stylesheet to load
   * @return              The parsed stylesheet
   * @throws Exception    If the stylesheet could not be loaded.
   */
  public Templates find(String path)
    throws Exception 
  {
    return (Templates)super.find(path);
  }

  /**
   * Enable or disable profiling (only affects stylesheets that are
   * not already cached). If the factory is null, profiling is
   * disabled.
   */
  public void enableProfiling(TraceListenerFactory tlf) {
    traceListenerFactory = tlf;
  }

  /**
   * Load and parse a stylesheet from the filesystem.
   *
   * @param  key          (String)Filesystem path of the stylesheet to load
   * @return              The parsed stylesheet
   * @throws Exception    If the stylesheet could not be loaded.
   */
  protected synchronized Object generate(Object key)
    throws Exception 
  {
    assert dependencyReceiver == null : "stylesheet cache should only have dependencyReceiver " +
      "during external calls, not during find().";
    if (dependencyChecking)
      dependencyReceiver = this;

    try 
    {
      String path = (String)key;
      File file = new File(path);
      if (dependencyChecking)
        addDependency(new FileDependency(file));
      if (!path.startsWith("http:") && !file.canRead())
        throw new GeneralException("Cannot read stylesheet: " + path);

      // Set up the profiling listener, if profiling is enabled
      if (traceListenerFactory != null) {
        TraceListener listener = traceListenerFactory.createListener();
        factory.setAttribute(FeatureKeys.TRACE_LISTENER, listener);
        factory.setAttribute(FeatureKeys.LINE_NUMBERING, Boolean.TRUE);
      }
  
      // Load that stylesheet!
      String url;
      if (path.startsWith("http:"))
        url = path;
      else
        url = file.toURL().toString();
      Templates x = factory.newTemplates(new SAXSource(new InputSource(url)));
      if (x == null)
        throw new TransformerException("Cannot read stylesheet: " + path);

      return x;
    }
    finally {
      dependencyReceiver = null;
    }
  } // generate()

  /** Prints out useful debugging info */
  protected void logAction(String action, Object key, Object value) {
    Trace.debug("StylesheetCache: " + action + ". Path=" + (String)key);
  }

  /**
   * While loading a stylesheet, we record all the sub-stylesheets
   * referenced by it, so that we can form a list of all the dependencies.
   * That way, if any of them are changed, the stylesheet will be auto-
   * matically reloaded.
   *
   * We do it by implementing a pass-through URIResolver that adds a
   * dependency and then does the normal URIResolver work.
   */
  private static class DepResolver implements URIResolver 
  {
    /**
     * Constructor.
     *
     * @param cache         The cache to add dependencies to
     * @param realResolver  The URIResolver that does the resolution
     */
    DepResolver(StylesheetCache cache, URIResolver realResolver) {
      this.cache = cache;
      this.realResolver = realResolver;
    }

    /**
     * Resolve a URI, and add a dependency for it to the cache.
     *
     * @param href  Full or partial hyperlink reference
     * @param base  Base URI of the document
     * @return      A Source representing the resolved URI.
     */
    public Source resolve(String href, String base)
      throws TransformerException 
    {
      if (href.indexOf(' ') >= 0)
        href = href.replaceAll(" ", "%20");

      if (base != null && base.indexOf(' ') >= 0)
        base = base.replaceAll(" ", "%20");

      // First, do the real resolution.
      Source src = realResolver.resolve(href, base);

      // If it's a file, add a dependency on it.
      if (src != null) 
      {
        String sysId = src.getSystemId();
        if (sysId != null && sysId.startsWith("file:")) 
        {
          // Be careful to pay attention if the cache is locked, so as to 
          // avoid leaking dependencies between threads.
          //
          synchronized (cache)
          {
            if (cache.dependencyReceiver != null) {
              String path = sysId.substring("file:".length());
              while (path.startsWith("//"))
                path = path.substring(1);
              cache.dependencyReceiver.addDependency(new FileDependency(path));
            }
          }
        }
      }

      // And we're done.
      return src;
    } // resolve()

    /** The cache to add dependencies to */
    StylesheetCache cache;

    /** Does the work of resolving the URI's */
    URIResolver realResolver;
  } // class DepResolver
} // class StylesheetCache

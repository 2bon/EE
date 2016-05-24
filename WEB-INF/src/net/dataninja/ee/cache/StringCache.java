package net.dataninja.ee.cache;


/**
net.dataninja copyright statement
 */

/**
 * This class caches mappings from a string to a string.
 */
public class StringCache extends SimpleCache 
{
  /**
   * Constructor - establishes the cache.
   *
   * @param cacheName     Name to print out in logAction()
   * @param maxEntries    Max # of entries before old ones are flushed.
   * @param maxTime       Max age (in seconds) of entries before flushed.
   */
  public StringCache(String cacheName, int maxEntries, int maxTime) {
    super(maxEntries, maxTime);
    this.cacheName = cacheName;
  }

  /**
   * Get the value corresponding to the given key
   *
   * @param key   The key to look for
   * @return      Corresponding value, or null if key not found.
   */
  public String get(String key) {
    return (String)super.get(key);
  }

  /** Print out useful debug info */
  protected void logAction(String action, Object key, Object value) 
  {
    //TextServlet.logPrint( 2, cacheName + ": " + action + 
    //                     ". key=" + (String)key );
  }

  /** Name of the cache, printed by logAction() */
  public String cacheName;
} // class StringCache

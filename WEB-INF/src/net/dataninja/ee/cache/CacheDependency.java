package net.dataninja.ee.cache;


/**
net.dataninja copyright statement
 */

/**
 * CacheDependency represents a dependency on an entry of the same or another
 * cache. If the cache entry changes, disappears, or its dependencies become
 * stale, then this dependency also becomes stale.
 */
public class CacheDependency extends Dependency 
{
  /**
   * Constructor.
   *
   * @param cache     The cache containing the entry to depend on
   * @param key       Key value to depend on within that cache.
   */
  public CacheDependency(Cache cache, Object key) {
    this.cache = cache;
    this.key = key;
    this.lastSet = cache.lastSet(key);
  }

  /**
   * Checks if the dependency is still valid. If the cache entry has changed,
   * disappeared, or has invalid dependencies, then this dependency is stale.
   *
   * @return  true iff the dependency is still fresh.
   */
  public boolean validate() {
    return (cache.has(key) && cache.lastSet(key) == lastSet &&
            cache.dependenciesValid(key));
  }

  /** The cache we're depending on */
  public Cache cache;

  /** The key within that cache we're depending on */
  public Object key;

  /** The set time of the cache entry when this dependency was created. */
  public long lastSet;
} // class CacheDependency

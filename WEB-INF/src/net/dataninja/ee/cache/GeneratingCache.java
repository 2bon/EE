package net.dataninja.ee.cache;


/**
net.dataninja copyright statement
 */

/**
 * A cache that generates an entry if one isn't found. The generate()
 * method must be supplied by the derived class.
 */
public abstract class GeneratingCache<K,V> extends Cache<K,V> 
{
  /**
   * Constructor - sets up the parameters of the cache.
   *
   * @param maxEntries    Maximum # of entries. Beyond this, older ones
   *                      will be removed. Zero means no limit.
   * @param maxTime       Time (in seconds) an entry can stay in the cache
   *                      without being used. Entries older than this will
   *                      be removed. Zero means no limit.
   */
  public GeneratingCache(int maxEntries, int maxTime) {
    super(maxEntries, maxTime);
  }

  /**
   * Check the cache for an entry matching the given key. If not found,
   * one is generated.
   *
   * @param key   The key to look up
   * @return      Value corresponding to that key. Never null.
   */
  public synchronized V find(K key)
    throws Exception 
  {
    // If we have already generated the value for this key, freshen the
    // entry and return it.
    //
    if (has(key)) {
      ListEntry entry = (ListEntry)keyMap.get(key);
      return entry.value;
    }

    // Otherwise, create an entry and generate a value for it.
    curEntry = new ListEntry();
    curEntry.key = key;
    curEntry.value = generate(key);

    // Add it to the age list (at the tail, since it's the most recently
    // used).
    curEntry.lastUsedTime = curEntry.setTime = System.currentTimeMillis();
    ageList.addTail(curEntry);

    // Add it to the key map and log the action.
    keyMap.put(key, curEntry);
    logAction("Generated", key, curEntry.value);

    // Clear the current entry to prevent any future refs to it.
    V value = curEntry.value;
    curEntry = null;

    // Since we've modified the age list, clean up if necessary.
    cleanup();

    // And return the generated value.
    return value;
  } // find()

  /**
   * Can be called by the generate() method to add a dependency to the
   * key being generated.
   *
   * @param d     The dependency to add
   */
  public void addDependency(Dependency d) {
    assert curEntry != null : "addDependency() may only be called from within generate()";
    curEntry.dependencies.add(d);
  }

  /**
   * Called when find() fails to locate an entry for the given key. This
   * method must be supplied by the derived class, and must produce a value
   * for the key, or throw an exception if it can't.
   *
   * @param   key         The key to generate a value for.
   * @return              The value for that key
   * @throws Exception    If a value cannot be generated for any reason.
   */
  protected abstract V generate(K key)
    throws Exception;

  /** The entry being generated */
  private ListEntry curEntry;
} // class GeneratingCache

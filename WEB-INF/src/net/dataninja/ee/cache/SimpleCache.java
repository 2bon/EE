package net.dataninja.ee.cache;


/**
net.dataninja copyright statement
 */

/**
 * A cache that holds key/value pairs. The value is specifed when a key is
 * added to the cache.
 */
public class SimpleCache<K,V> extends Cache<K,V> 
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
  public SimpleCache(int maxEntries, int maxTime) {
    super(maxEntries, maxTime);
  }

  /**
   * Set the value for a key. If already present, the old value is replaced.
   *
   * @param key   Key to set the value for
   * @param value Value for that key.
   */
  public void set(K key, V value) {
    set(key, value, null);
  }

  /**
   * Set the value for a key, optionally adding a dependency for it.
   * If the key is already present, the old value is replaced.
   *
   * @param key           The key that will be used to look up the value
   * @param value         The value to associate with that key
   * @param dependency    A dependency to add to the key, or null for none.
   */
  public synchronized void set(K key, V value, Dependency dependency) 
  {
    ListEntry entry;

    // If we already have this key, replace the value.
    if (has(key)) {
      entry = (ListEntry)keyMap.get(key);
      entry.value = value;
      entry.dependencies.clear();
      if (dependency != null)
        entry.dependencies.add(dependency);
      entry.setTime = System.currentTimeMillis();
      logAction("Replaced", key, value);
      return;
    }

    // Otherwise, add a new entry.
    entry = new ListEntry();
    entry.key = key;
    entry.value = value;
    if (dependency != null)
      entry.dependencies.add(dependency);

    // Add it to the age list (at the tail, since it's the most recently
    // used).
    entry.lastUsedTime = entry.setTime = System.currentTimeMillis();
    ageList.addTail(entry);

    // Add it to the key map and log the action.
    keyMap.put(key, entry);
    logAction("Added", key, value);

    // Since we've modified the age list, clean up if necessary.
    cleanup();
  } // set()

  /**
   * Gets the value associated with a key, or null if none.
   *
   * @param key       The key to look for
   * @return          The value for that key, or null if the key isn't
   *                  in the cache.
   */
  public synchronized V get(K key) {
    if (has(key))
      return ((ListEntry)keyMap.get(key)).value;
    else
      return null;
  } // get()

  /**
   * Add a dependency to an existing entry. If the dependency later becomes
   * invalid, the key will be removed from the cache.
   *
   * @param key       The key to add a dependency to
   * @param d         The dependency to add to it.
   */
  public synchronized void addDependency(K key, Dependency d) {
    if (!has(key))
      return;
    ((ListEntry)keyMap.get(key)).dependencies.add(d);
  } // addDependency()
} // class SimpleCache

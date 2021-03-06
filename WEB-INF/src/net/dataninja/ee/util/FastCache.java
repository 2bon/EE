package net.dataninja.ee.util;


/*
dataninja copyright statement
 */

/**
 * A fast but inflexible cache where the keys are anything, the size
 * is fixed, and a crude LRU policy is enforced. Handles consecutive keys
 * gracefully. Doesn't support resizing, deletion, or iteration (not that these
 * operations would be hard, just that they haven't been needed so far.)
 *
 * @author Rick Li
 */
public class FastCache<K, V>
{
  private int size;
  private FastHashMap<K, V> oldHash;
  private FastHashMap<K, V> newHash;

  /**
   * Construct a new cache. Basically, two hash tables are used, each with
   * the given capacity. When one fills up, the other is thrown out. A
   * Least-Recently-Used policy is effected by migrating entries from the old
   * hash to the new hash when they are accessed through get().
   *
   * @param size  How large to make each of the two internal hash tables.
   */
  public FastCache(int size) {
    this.size = size;
    clear();
  } // FastCache()

  /** Clears all entries from the cache */
  public void clear() {
    oldHash = new FastHashMap(1);
    newHash = new FastHashMap(size);
  }

  /** Check whether the given key is present in the cache */
  public boolean contains(K key) {
    return newHash.contains(key) || oldHash.contains(key);
  }

  /** Retrieve the value for the given key, or null if not found. */
  public V get(K key) 
  {
    // First, check the new hash
    V retVal = newHash.get(key);
    if (retVal == null) 
    {
      // Darn. Check the old hash. If it's there, put it into the new hash
      // since it's been used recently. We don't need to worry about 
      // deleting the old entry, as it will disappear eventually when the 
      // hashes are swapped.
      //
      retVal = oldHash.get(key);
      if (retVal != null)
        put(key, retVal);
    }

    // All done.
    return retVal;
  } // get()

  /**
   * Add a key/value pair to the cache. May result in pushing older items
   * out of the cache.
   */
  public void put(K key, V val) 
  {
    // If the new hash is full, swap and create a new empty hash.
    if (newHash.size() >= size) {
      oldHash = newHash;
      newHash = new FastHashMap(size);
    }

    // Now that we're sure there's room, put it in the new hash.
    newHash.put(key, val);
  } // put()

  /**
   * Basic regression test
   */
  public static final Tester tester = new Tester("FastCache") 
  {
    protected void testImpl() 
    {
      FastHashMap.tester.test();

      FastCache cache = new FastCache(3);

      cache.put("1", "a");
      cache.put("2", "b");
      cache.put("3", "c");
      assert cache.contains("1");
      assert cache.contains("2");
      assert cache.contains("3");

      cache.put("4", "d");
      assert cache.get("2").equals("b");
      cache.put("5", "e");
      cache.put("6", "f");
      assert !cache.contains("1");
      assert cache.contains("2");
      assert cache.contains("5");
      assert cache.contains("6");
    } // testImpl()
  };
} // class FastCache

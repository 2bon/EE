package net.dataninja.ee.util;

import org.apache.lucene.util.Prime;


/**
net.dataninja copyright statement
 */

/**
 * A fast but inflexible hash table where the keys are strings and the size
 * is fixed. Handles consecutive keys gracefully, but doesn't support resizing,
 * deletion, or iteration.
 *
 * @author Rick Li
 */
public class FastHashMap<K, V>
{
  private final int hashSize;
  private final Ent[] ents;
  private int curSize;

  /**
   * Create the hash table that can comfortably hold the specified number
   * of entries. The actual table is created to be the smallest prime
   * greater than size*2.
   *
   * @param maxSize  Max # of entries
   */
  public FastHashMap(int maxSize) {
    this.hashSize = Prime.findAfter(maxSize * 2);
    ents = new Ent[hashSize];
    curSize = 0;
  } // constructor

  /**
   * Sets the entry for the given key number. If one already exists, the old
   * value is replaced. Using null for the value can be useful if one only
   * needs to check for key presence using contains().
   */
  public void put(K key, V val) 
  {
    int bucket = hashSlot(key);

    // Is there already an entry for this key?
    Ent<K,V> e;
    for (e = ents[bucket]; e != null; e = e.next) 
    {
      if (key.equals(e.key)) {
        e.val = val;
        return;
      }
    } // for e

    // Okay, make a new entry
    e = new Ent();
    e.key = key;
    e.val = val;

    // And link it in.
    e.next = ents[bucket];
    ents[bucket] = e;

    // All done.
    ++curSize;
  } // put()

  /** Calculate the hash slot for a given key */
  private final int hashSlot(K key) {
    int code = key.hashCode();
    if (code >= 0)
      return code % hashSize;
    return (-code) % hashSize;
  } // hash()

  /**
   * Checks if the hash contains an entry for the given key.
   */
  public boolean contains(K key) {
    for (Ent e = ents[hashSlot(key)]; e != null; e = e.next)
      if (key.equals(e.key))
        return true;
    return false;
  } // contains()

  /**
   * Retrieves the entry for the given key.
   *
   * @param key   Key to look for
   * @return      The associated value, or null if not found.
   */
  public V get(K key) {
    for (Ent<K, V> e = ents[hashSlot(key)]; e != null; e = e.next)
      if (key.equals(e.key))
        return e.val;
    return null;
  } // get()

  /** Tells how many entries are currently in the hash table */
  public int size() {
    return curSize;
  } // size()

  /**
   * Keeps track of a single entry in the hash table. Can be linked to form
   * a chain.
   */
  private static class Ent<K, V> {
    K key;
    V val;
    Ent next;
  } // private class Ent

  /**
   * Basic regression test
   */
  public static final Tester tester = new Tester("FastHashMap") 
  {
    protected void testImpl() 
    {
      FastHashMap hash = new FastHashMap(5);

      hash.put("100", "hello");
      assert hash.contains("100");
      assert !hash.contains("111");
      assert hash.get("100").equals("hello");
      assert hash.size() == 1;

      hash.put("200", "foo");
      hash.put("211", "bar");
      assert hash.contains("100");
      assert hash.contains("200");
      assert hash.contains("211");
      assert !hash.contains("111");
      assert !hash.contains("212");
      assert hash.size() == 3;
      assert hash.get("100").equals("hello");
      assert hash.get("200").equals("foo");
      assert hash.get("211").equals("bar");
    } // testImpl()
  };
} // class FastHashMap

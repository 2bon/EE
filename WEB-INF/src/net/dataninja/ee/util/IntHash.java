package net.dataninja.ee.util;

import org.apache.lucene.util.Prime;


/**
net.dataninja copyright statement
 */

/**
 * A fast but inflexible hash table where the keys are integers and the size
 * is fixed. Handles consecutive keys gracefully, but doesn't support resizing,
 * deletion, or iteration.
 *
 * @author Rick Li
 */
public class IntHash 
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
  public IntHash(int maxSize) {
    this.hashSize = Prime.findAfter(maxSize * 2);
    ents = new Ent[hashSize];
    curSize = 0;
  } // constructor

  /**
   * Sets the entry for the given key number. If one already exists, the old
   * value is replaced. Using null for the value can be useful if one only
   * needs to check for key presence using contains().
   */
  public void put(int key, Object val) 
  {
    int bucket = key % hashSize;

    // Is there already an entry for this key?
    Ent e;
    for (e = ents[bucket]; e != null; e = e.next) 
    {
      if (key == e.key) {
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

  /**
   * Checks if the hash contains an entry for the given key.
   */
  public boolean contains(int key) {
    for (Ent e = ents[key % hashSize]; e != null; e = e.next)
      if (key == e.key)
        return true;
    return false;
  } // contains()

  /**
   * Retrieves the entry for the given key.
   *
   * @param key   Key to look for
   * @return      The associated value, or null if not found.
   */
  public Object get(int key) {
    for (Ent e = ents[key % hashSize]; e != null; e = e.next)
      if (key == e.key)
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
  private class Ent {
    int key;
    Object val;
    Ent next;
  } // private class Ent

  /**
   * Basic regression test
   */
  public static final Tester tester = new Tester("IntHash") 
  {
    protected void testImpl() 
    {
      IntHash hash = new IntHash(5);

      hash.put(100, "hello");
      assert hash.contains(100);
      assert !hash.contains(111);
      assert hash.get(100).equals("hello");
      assert hash.size() == 1;

      hash.put(200, "foo");
      hash.put(211, "bar");
      assert hash.contains(100);
      assert hash.contains(200);
      assert hash.contains(211);
      assert !hash.contains(111);
      assert !hash.contains(212);
      assert hash.size() == 3;
      assert hash.get(100).equals("hello");
      assert hash.get(200).equals("foo");
      assert hash.get(211).equals("bar");
    } // testImpl()
  };
} // class Inthash

package net.dataninja.ee.util;


/**
net.dataninja copyright statement
 */
import java.util.HashMap;
import java.util.Vector;

/**
 * Implements a simple mapping, from object keys to integers. Each new key
 * is assigned a consecutive number, starting at zero.
 *
 * @author Rick Li
 */
public class ConsecutiveMap 
{
  /** Mapping used to keep the unique set of keys */
  private HashMap map = new HashMap(100);

  /** Vector of all unique keys, in order of addition */
  private Vector inOrder = new Vector(100);

  /**
   * If the key is already present in the map, return its assigned number.
   * Otherwise, add it and assign it a new number.
   *
   * @param key   The key to look up
   * @return      A number associated with that key
   */
  public int put(Object key) 
  {
    Integer num = (Integer)map.get(key);
    if (num == null) {
      num = Integer.valueOf(inOrder.size());
      inOrder.add(key);
      map.put(key, num);
    }

    return num.intValue();
  } // put()

  /**
   * Retrieve the namecode for the given key. If not found, returns -1.
   */
  public int get(Object key) {
    Object num = map.get(key);
    if (num == null)
      return -1;
    return ((Integer)num).intValue();
  } // get()

  /**
   * Check if the given key is present in the map yet.
   */
  public boolean has(Object key) {
    return map.get(key) != null;
  } // has()

  /**
   * Retrieve an array of all the keys, ordered by consecutive number.
   */
  public Object[] getArray() {
    return inOrder.toArray();
  } // getArray()
} // class ConsecutiveMap

package net.dataninja.ee.util;


/**
net.dataninja copyright statement
 */
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Maintains a list of key/value pairs. Can be easily iterated over or
 * searched by key.
 */
public class AttribList 
{
  /**
   * The list is stored as a linked list. Not so fast to iterate, but fast
   * to add/remove.
   */
  private LinkedList list = new LinkedList();

  /**
   * Add a key/value pair to the list. Note: does not check for duplicates!
   *
   * @param key       Key identifier
   * @param value     Value to associate with that key
   */
  public void put(String key, String value) {
    list.add(new Attrib(key, value));
  }

  /**
   * Retrieves the value associated with the given key, or null if not
   * present.
   */
  public String get(String key) 
  {
    for (Iterator iter = iterator(); iter.hasNext();) {
      Attrib att = (Attrib)iter.next();
      if (att.key.equals(key))
        return att.value;
    }
    return null;
  }

  /** Get an iterator on the list */
  public Iterator iterator() {
    return list.iterator();
  }

  /** Remove all key/value pairs from the list */
  public void clear() {
    list.clear();
  }

  /** Check if the list is empty */
  public boolean isEmpty() {
    return list.isEmpty();
  }

  /** Return the number of key/value pairs in the list */
  public int size() {
    return list.size();
  }
} // class AttribList

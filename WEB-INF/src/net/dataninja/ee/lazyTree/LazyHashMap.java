package net.dataninja.ee.lazyTree;

/**
dataninja copyright statement
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import net.dataninja.ee.util.DiskHashReader;
import net.dataninja.ee.util.PackedByteBuf;

/**
 * A very light wrapper that puts a Map interface over a DiskHashReader
 * for use with lazy keys.
 * 
 * @author Rick Li
 */
public class LazyHashMap implements Map
{
  private LazyDocument doc;
  private DiskHashReader reader;

  /** Construct a hash map to read keys from a {@link DiskHashReader} */
  public LazyHashMap(LazyDocument doc, DiskHashReader reader) {
    this.doc = doc;
    this.reader = reader;
  }
  
  /**
   * Get the list of nodes associated with a given key.
   */
  public Object get(Object key) 
  {
    PackedByteBuf buf;
    try {
      buf = reader.find(key.toString());
    }
    catch (IOException e) {
      throw new RuntimeException("Error encountered reading key from lazy tree file: ", e);
    }
    if (buf == null)
      return null;

    int nNodes = buf.readInt();
    ArrayList nodes = new ArrayList(nNodes);

    int curNum = 0;
    for (int i = 0; i < nNodes; i++) {
      curNum += buf.readInt();
      nodes.add(doc.getNode(curNum));
    }

    return nodes;
  }

  public void clear() {
    throw new UnsupportedOperationException();
  }

  public boolean containsKey(Object key) {
    throw new UnsupportedOperationException();
  }

  public boolean containsValue(Object value) {
    throw new UnsupportedOperationException();
  }

  public Set entrySet() {
    throw new UnsupportedOperationException();
  }
  
  public boolean isEmpty() {
    throw new UnsupportedOperationException();
  }
  
  public Set keySet() {
    throw new UnsupportedOperationException();
  }

  public Object put(Object key, Object value) {
    throw new UnsupportedOperationException();
  }

  public void putAll(Map t) {
    throw new UnsupportedOperationException();
  }

  public Object remove(Object key) {
    throw new UnsupportedOperationException();
  }

  public int size() {
    throw new UnsupportedOperationException();
  }

  public Collection values() {
    throw new UnsupportedOperationException();
  }

}

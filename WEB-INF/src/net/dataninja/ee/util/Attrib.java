package net.dataninja.ee.util;


/**
net.dataninja copyright statement
 */

/**
 * A simple key/value relationship. Typically stored within an
 * {@link AttribList}.
 */
public class Attrib 
{
  /** Default constructor */
  public Attrib() {
  }

  /** Fancy constructor - sets the key and the value */
  public Attrib(String key, String value) {
    this.key = key;
    this.value = value;
  }

  public boolean equals(Object o) {
    if (!(o instanceof Attrib))
      return false;
    return key.equals(((Attrib)o).key);
  }

  public String key;
  public String value;
} // class Attrib

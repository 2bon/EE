package net.dataninja.ee.util;


/**
net.dataninja copyright statement
 */

/**
 * All entries added to an {@link EmbeddedList} must implement this
 * interface. For an easy way to do so, extend the {@link LinkableImpl}
 * class instead.
 */
public interface Linkable 
{
  /** Get a reference to the next item in the chain */
  public Linkable getNext();

  /** Get a reference to the previous item in the chain */
  public Linkable getPrev();

  /** Get a reference to the EmbeddedList that owns this object */
  public EmbeddedList getOwner();

  /** Assign the next item in the chain */
  public void setNext(Linkable l);

  /** Assign the previous item in the chain */
  public void setPrev(Linkable l);

  /** Get a reference to the EmbeddedList that owns this object */
  public void setOwner(EmbeddedList o);
} // class Linkable

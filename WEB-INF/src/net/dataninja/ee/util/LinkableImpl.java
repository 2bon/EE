package net.dataninja.ee.util;


/**
net.dataninja copyright statement
 */

/** A simple implementation of the Linkable interface. */
public class LinkableImpl implements Linkable 
{
  /** Pointer to the next link in the chain */
  private Linkable nextLink;

  /** Pointer to the previous link in the chain */
  private Linkable prevLink;

  /** The list that owns this item */
  private EmbeddedList owner;

  public Linkable getNext() {
    return nextLink;
  }

  public Linkable getPrev() {
    return prevLink;
  }

  public EmbeddedList getOwner() {
    return owner;
  }

  public void setNext(Linkable l) {
    nextLink = l;
  }

  public void setPrev(Linkable l) {
    prevLink = l;
  }

  public void setOwner(EmbeddedList o) {
    owner = o;
  }
} // class LinkableImpl

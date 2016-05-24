package net.dataninja.ee.lazyTree;

/**
net.dataninja copyright statement
 */

/**
 * Represents a text node that has been (possibly) modified to reflect query
 * results.
 *
 * @author Rick Li
 */
public class SearchTextImpl extends TextImpl implements SearchNode 
{
  /** Construct */
  public SearchTextImpl(SearchTree tree) {
    document = tree;
  }
  
  /** Establish the node number */
  public void setNodeNum(int num) {
    nodeNum = num;
  }

  /** Establish the parent node number */
  public void setParentNum(int parentNum) {
    this.parentNum = parentNum;
  }

  /** Establish the next sibling node number */
  public void setNextSibNum(int num) {
    nextSibNum = num;
  }

  /** Establish the previous sibling node number */
  public void setPrevSibNum(int num) {
    prevSibNum = num;
  }

  /** Establish the text value for this node */
  public void setStringValue(String newText) {
    text = newText;
  }

  /**
   * Get a unique sequence number for this node. These are used for
   * sorting nodes in document order.
   */
  protected long getSequenceNumber() 
  {
    // If this node isn't virtual, do the normal thing.
    if (nodeNum <= SearchTree.VIRTUAL_MARKER)
      return super.getSequenceNumber();

    // Okay, find the next previous non-virtual node, and use its sequence
    // number as a base, to which we add the count of intervening virtual
    // nodes.
    //
    NodeImpl node = this;
    int count = 0;
    while ((node = node.getPreviousInDocument()) != null) {
      ++count;
      if (node.nodeNum <= SearchTree.VIRTUAL_MARKER)
        return node.getSequenceNumber() + (count << 16);
    }
    assert false : "Virtual node must be preceeded by some real node";
    return 0;
  } // getSequenceNumber()
} // class SearchTextImpl

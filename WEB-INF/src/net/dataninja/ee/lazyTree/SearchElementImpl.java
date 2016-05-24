package net.dataninja.ee.lazyTree;


/**
net.dataninja copyright statement
 */

import net.sf.saxon.event.Receiver;
import net.sf.saxon.om.Axis;
import net.sf.saxon.om.AxisIterator;
import net.sf.saxon.pattern.NodeTest;
import net.sf.saxon.trans.XPathException;

/**
 * Represents an element that has been (possibly) modified to reflect search
 * results. Handles adding the &lt;ee:hitCount&gt; attribute so the client
 * can easily show how many hits a given section has within it.
 *
 * @author Rick Li
 */
public class SearchElementImpl extends ElementImpl implements SearchElement 
{
  boolean specialAttrChecked = false;

  SearchElementImpl(SearchTree tree) {
    prevSibNum = nextSibNum = childNum = nameSpace = -1;
    document = tree;
  }

  /** Set the node number for this node. */
  public void setNodeNum(int nodeNum) {
    this.nodeNum = nodeNum;
  }

  /** Allocate the attribute array. */
  public void allocateAttributes(int nAttrs) {
    attrNames = new int[nAttrs];
    attrValues = new String[nAttrs];
  }

  /** Set an attribute */
  public void setAttribute(int attrNum, int nameCode, String value) {
    attrNames[attrNum] = nameCode;
    attrValues[attrNum] = value;
  }

  /** Establish the parent node */
  public void setParentNum(int parentNum) {
    this.parentNum = parentNum;
  }

  /** Establish the child node number */
  public void setChildNum(int num) {
    childNum = num;
  }

  /** Establish the next sibling node number */
  public void setNextSibNum(int num) {
    nextSibNum = num;
  }

  /** Establish the previous sibling node number */
  public void setPrevSibNum(int num) {
    prevSibNum = num;
  }

  /** Establish a name for this node */
  public void setNameCode(int code) {
    nameCode = code;
  }

  /**
   * Return an enumeration over the nodes reached by the given axis from this
   * node
   *
   * @param axisNumber The axis to be iterated over
   * @param nodeTest A pattern to be matched by the returned nodes
   * @return an AxisIterator that scans the nodes reached by the axis in turn.
   */
  public AxisIterator iterateAxis(byte axisNumber, NodeTest nodeTest) 
  {
    // Make sure our special attribute has been added before allowing access
    // to the attributes.
    //
    if (axisNumber == Axis.ATTRIBUTE)
      addSpecialAttrib();

    // That done, we just do the normal thing.
    return super.iterateAxis(axisNumber, nodeTest);
  } // iterateAxis()

  /**
   * Get the value of a given attribute of this node
   *
   * @param fingerprint The fingerprint of the attribute name
   * @return the attribute value if it exists or null if not
   */
  public String getAttributeValue(int fingerprint) 
  {
    // Make sure our special attribute has been added before allowing access
    // to the attributes.
    //
    addSpecialAttrib();
    return super.getAttributeValue(fingerprint);
  } // getAttributeValue()

  public void copy(Receiver out, int whichNamespaces, boolean copyAnnotations,
                   int locationId)
    throws XPathException 
  {
    // Make sure our special attribute has been added before allowing access
    // to the attributes.
    //
    addSpecialAttrib();
    super.copy(out, whichNamespaces, copyAnnotations, locationId);
  } // copy()

  // If the 'hitCount' attribute hasn't been added yet, this code does it.
  private void addSpecialAttrib() 
  {
    // Have we already done the work? If so, skip out.
    if (specialAttrChecked)
      return;
    specialAttrChecked = true;

    // Don't do this to any marked node (virtual, snippet, etc.)
    if (nodeNum >= SearchTree.MARKER_BASE)
      return;

    // Okay, figure out how many. If none, skip out (unless this is the
    // root node or its first child, in which case we *always* record the
    // number, even if zero.
    //
    SearchTree tree = (SearchTree)document;
    int firstHit = tree.findFirstHit(nodeNum);
    int lastHit = tree.findLastHit(nodeNum);
    int count = lastHit - firstHit;
    if (count == 0 && nodeNum > 1)
      return;

    // Gotta add an attribute now.
    int nAttrs = attrNames != null ? attrNames.length : 0;
    int[] names2 = new int[nAttrs + 2];
    if (attrNames != null)
      System.arraycopy(attrNames, 0, names2, 0, nAttrs);
    attrNames = names2;

    String[] values2 = new String[nAttrs + 2];
    if (attrValues != null)
      System.arraycopy(attrValues, 0, values2, 0, nAttrs);
    attrValues = values2;

    attrNames[nAttrs] = tree.xtfHitCountAttrCode;
    attrValues[nAttrs] = Integer.toString(count);
    nAttrs++;

    attrNames[nAttrs] = tree.xtfFirstHitAttrCode;
    attrValues[nAttrs] = Integer.toString(firstHit + 1); // XSLT is 1-based
    nAttrs++;
  } // addSpecialAttrib()

  /**
   * Gets the sequence number of this element, used for sorting nodes in
   * document order.
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

  /**
  * Output all namespace nodes associated with this element.
  * @param out The relevant outputter
  * @param includeAncestors True if namespaces associated with ancestor
  */
  public void sendNamespaceDeclarations(Receiver out, boolean includeAncestors)
    throws XPathException 
  {
    super.sendNamespaceDeclarations(out, includeAncestors);

    // Be sure to output the ee namespace at the top level.
    if (nodeNum <= 1)
      out.namespace(((SearchTree)document).xtfNamespaceCode, 0);
  } // sendNamespaceDeclarations()
} // class SearchElementImpl

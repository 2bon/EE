package net.dataninja.ee.lazyTree;


/**
net.dataninja copyright statement
 */

/**
 * This interface is implemented by SearchElement and ProxyElement. It allows
 * setting the attributes, node number, etc. of an element.
 *
 * @author Rick Li
 */
public interface SearchElement extends SearchNode 
{
  /** Set the name code for an element */
  void setNameCode(int nameCode);

  /** Set the number of the first child node */
  void setChildNum(int num);

  /** Allocate the array for pre-computed attributes */
  void allocateAttributes(int nAttrs);

  /** Set a particular attribute in the array */
  void setAttribute(int attrNum, int nameCode, String value);
} // interface SearchElement

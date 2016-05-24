package net.dataninja.ee.lazyTree;

/**
net.dataninja copyright statement
 */

/**
 * Basic interface shared by SearchElement and ProxyElement, which is useful
 * for code that can create either one without having to know their internal
 * structure.
 *
 * @author Rick Li
 */
public interface SearchNode 
{
  /** Establish the node's number */
  void setNodeNum(int nodeNum);

  /** Establish the parent node */
  void setParentNum(int parentNum);

  /** Establish the next sibling node number */
  void setNextSibNum(int num);

  /** Establish the previous sibling node number */
  void setPrevSibNum(int num);
}

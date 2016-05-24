package net.dataninja.ee.lazyTree;


/**
net.dataninja copyright statement
 */

/**
 * Enumeration of the flags we store for a node.
 */
public class Flag {
  public static final int HAS_NAMECODE = 1 << 0;
  public static final int HAS_PARENT = 1 << 1;
  public static final int HAS_PREV_SIBLING = 1 << 2;
  public static final int HAS_NEXT_SIBLING = 1 << 3;
  public static final int HAS_CHILD = 1 << 4;
  public static final int HAS_ALPHA = 1 << 5;
  public static final int HAS_BETA = 1 << 6;
}

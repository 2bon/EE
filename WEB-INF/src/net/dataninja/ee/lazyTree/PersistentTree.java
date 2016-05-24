package net.dataninja.ee.lazyTree;

import java.io.IOException;

/**
net.dataninja copyright statement
 */

/**
 * General interface for a tree that is disk-based, and should be closed
 * after use.
 *
 * @author Rick Li
 */
public interface PersistentTree 
{
  /** This should be called when done using the tree, to close disk files */
  void close();

  /** Print out a profile (if one was collected) */
  void printProfile()
    throws IOException;

  /** Establishes whether nodes should be held in RAM, or only held by
   *  soft references.
   *
   *  @param flag     True to hold nodes for the life of the tree, false
   *                  to hold only soft references to them.
   */
  void setAllPermanent(boolean flag);
} // interface PersistentTree

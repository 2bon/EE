package net.dataninja.ee.textEngine.facet;


/*
net.dataninja copyright statement
 */

/** Select top-level (root) groups */
public class RootSelector extends ChildSelector 
{
  public String toString() {
    return "root -> " + next.toString();
  }
}

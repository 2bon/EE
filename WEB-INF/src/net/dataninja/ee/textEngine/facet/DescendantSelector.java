package net.dataninja.ee.textEngine.facet;


/*
net.dataninja copyright statement
 */

/** Select all the group and all its descendants of the incoming groups */
public class DescendantSelector extends GroupSelector 
{
  public void process(int parent) 
  {
    // First, select this group
    next.process(parent);

    // Use 'counts' instead of 'data', so we get properly sorted order.
    int child = counts.child(parent);
    while (child >= 0) {
      if (conservative || counts.shouldInclude(child))
        process(child); // recursively process grandchildren, etc.
      child = counts.sibling(child);
    }
  }

  public String toString() {
    return "descendants -> " + next.toString();
  }
}

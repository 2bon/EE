package net.dataninja.ee.textEngine.facet;


/*
net.dataninja copyright statement
 */

/** Select children of the incoming groups */
public class ChildSelector extends GroupSelector 
{
  public void process(int parent) 
  {
    // Use 'counts' instead of 'data', so we get properly sorted order.
    int child = counts.child(parent);
    while (child >= 0) {
      if (conservative || counts.shouldInclude(child))
        next.process(child);
      child = counts.sibling(child);
    }
  }

  public String toString() {
    return "children -> " + next.toString();
  }
}

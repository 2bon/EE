package net.dataninja.ee.textEngine.facet;


/*
net.dataninja copyright statement
 */
import java.util.HashSet;

/** Pass only groups that are singletons, i.e. have no selected siblings. */
public class SingletonSelector extends GroupSelector 
{
  private HashSet parents = new HashSet();

  public void reset(boolean conservative) {
    super.reset(conservative);
    parents.clear();
  }

  public void process(int group) 
  {
    // In conservative mode, we have to select the entire tree
    if (conservative) {
      next.process(group);
      return;
    }
    
    // Normal (non-conservative mode)... Have we seen this parent before?
    // If so, ignore it.
    //
    int parent = counts.parent(group);
    Integer parentKey = Integer.valueOf(parent);
    if (parents.contains(parentKey))
      return;

    // Okay, count the children of our parent.
    int childCount = 0;
    for (int kid = counts.child(parent); kid >= 0; kid = counts.sibling(kid)) {
      if (!counts.shouldInclude(kid))
        continue;
      if (!counts.isSelected(group))
        continue;
      ++childCount;
    }
    
    // If we're the only child, it's a go.
    if (childCount == 1)
      next.process(group);

    // And record that we've finished this parent now.
    parents.add(parentKey);
  } // process()

  public String toString() {
    return "singleton -> " + next.toString();
  }
}

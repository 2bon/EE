package net.dataninja.ee.textEngine.facet;


/*
net.dataninja copyright statement
 */
import java.util.HashSet;

/** Select all siblings of each incoming group. */
public class SiblingSelector extends GroupSelector 
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

    // Okay, process all the children under this parent.
    for (int kid = counts.child(parent); kid >= 0; kid = counts.sibling(kid)) {
      if (!counts.shouldInclude(kid))
        continue;
      next.process(kid);
    }

    // And record that we've finished this parent now.
    parents.add(parentKey);
  } // process()

  public String toString() {
    return "siblings -> " + next.toString();
  }
}

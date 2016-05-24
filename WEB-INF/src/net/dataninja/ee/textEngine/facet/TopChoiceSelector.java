package net.dataninja.ee.textEngine.facet;


/*
net.dataninja copyright statement
 */
import java.util.Vector;

/** Select the top level of the hierarchy that has a choice. */
public class TopChoiceSelector extends GroupSelector 
{
  private int bestLevel;
  private int bestParent;
  private Vector bestChildren = new Vector(10);
  private int[] firstChild;

  public void reset(boolean conservative) {
    super.reset(conservative);
    bestLevel = 999999999;
    bestParent = -1;
    firstChild = new int[counts.nGroups()];
  }

  public void process(int group) 
  {
    // In conservative mode, we have to select the entire tree
    if (conservative) {
      next.process(group);
      return;
    }

    // Normal (non-conservative mode)...
    int parent = counts.parent(group);

    // If we haven't seen this parent before, record the group as its first
    // child.
    //
    if (firstChild[parent] == 0) {
      firstChild[parent] = group;
      return;
    }

    // Ok, we know now that it has more than one child... If this is our 
    // current best candidate, simply add this child to its list.
    //
    if (parent == bestParent) {
      bestChildren.add(Integer.valueOf(group));
      return;
    }

    // Figure out its level.
    int level = 0;
    for (int g = parent; g >= 0; g = counts.parent(g))
      level++;

    // If it's not as good as the level we've found, skip it.
    if (level >= bestLevel)
      return;

    // We have a new parent that's better than we had before. We recorded the
    // first child already; this is the second one.
    //
    bestParent = parent;
    bestLevel = level;
    bestChildren.setSize(0);
    bestChildren.add(Integer.valueOf(firstChild[parent]));
    bestChildren.add(Integer.valueOf(group));
  } // process()

  public void flush() 
  {
    // If we found a level with choices...
    if (bestParent >= 0) 
    {
      // Okay, process the children at the best level we found.
      for (int i = 0; i < bestChildren.size(); i++)
        next.process(((Integer)bestChildren.elementAt(i)).intValue());
    }

    // Pass the flush on.
    next.flush();
  } // flush()

  public String toString() {
    return "topChoices -> " + next.toString();
  }
}

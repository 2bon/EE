package net.dataninja.ee.textEngine.facet;


/*
net.dataninja copyright statement
 */

/** Pass incoming groups to a number of selectors. */
public class UnionSelector extends GroupSelector 
{
  GroupSelector[] selectors;

  public UnionSelector(GroupSelector[] selectors) {
    this.selectors = selectors;
  }

  /** Set the counts to be used */
  public void setCounts(GroupCounts counts) {
    super.setCounts(counts);
    for (int i = 0; i < selectors.length; i++)
      selectors[i].setCounts(counts);
  }

  /** Reset the selector */
  public void reset(boolean conservative) {
    super.reset(conservative);
    for (int i = 0; i < selectors.length; i++)
      selectors[i].reset(conservative);
  }

  /** Process the given group. */
  public void process(int group) 
  {
    // This should only be called at the top level, so that the second
    // selector can rely on the first selector's results entirely.
    //
    assert group == 0 : "UnionSelector should only be top-level";

    // Okay, do each one in turn, processing and completely flushing it before
    // moving on to the next.
    //
    for (int i = 0; i < selectors.length; i++) {
      selectors[i].process(group);
      selectors[i].flush();
    }
  }

  /** Flush any remaining queued groups */
  public void flush() 
  {
    // Already flushed in process() above.
  }

  public String toString() 
  {
    StringBuffer buf = new StringBuffer();
    buf.append("union(");
    for (int i = 0; i < selectors.length; i++) {
      buf.append(selectors[i].toString());
      if (i < selectors.length - 1)
        buf.append("|");
    }
    buf.append(")");
    return buf.toString();
  }
}

package net.dataninja.ee.textEngine.facet;


/*
net.dataninja copyright statement
 */

/** Gather documents for all incoming groups. Always ends a selector chain */
public class DocsSelector extends GroupSelector 
{
  int start;
  int max;

  public DocsSelector(int start, int max) {
    this.start = start;
    this.max = max;
  }

  public void process(int group) {
    counts.selectGroup(group);
    counts.gatherDocs(group, start, max);
  }

  public String toString() {
    return "docs(" + start + "," + max + ")";
  }
}

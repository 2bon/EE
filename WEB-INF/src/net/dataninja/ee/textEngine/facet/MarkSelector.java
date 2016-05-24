package net.dataninja.ee.textEngine.facet;


/*
net.dataninja copyright statement
 */

/** Mark all incoming groups. Always ends a selector chain. */
public class MarkSelector extends GroupSelector 
{
  public void process(int group) {
    counts.selectGroup(group);
  }

  public String toString() {
    return "mark";
  }
}

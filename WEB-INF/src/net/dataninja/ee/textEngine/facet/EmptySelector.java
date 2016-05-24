package net.dataninja.ee.textEngine.facet;


/*
net.dataninja copyright statement
 */

/** Select empty or non-empty groups, according to a flag. */
public class EmptySelector extends GroupSelector 
{
  private boolean flag;

  public EmptySelector(boolean flag) {
    this.flag = flag;
  }

  public void process(int group) {
    boolean isEmpty = (counts.nDocHits(group) == 0);
    if (conservative || (isEmpty == flag))
      next.process(group);
  }

  public String toString() {
    return "empty(" + flag + ") -> " + next.toString();
  }
}

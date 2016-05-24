package net.dataninja.ee.textEngine.facet;


/*
net.dataninja copyright statement
 */

/** Select selected or non-selected groups, depending on a flag */
public class SelectedSelector extends GroupSelector 
{
  private boolean flag;

  public SelectedSelector(boolean flag) {
    this.flag = flag;
  }

  public void process(int group) {
    boolean isSelected = counts.isSelected(group);
    if (conservative || (isSelected == flag))
      next.process(group);
  }

  public String toString() {
    return "selected(" + flag + ") -> " + next.toString();
  }
}

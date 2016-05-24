package net.dataninja.ee.textEngine.facet;


/*
net.dataninja copyright statement
 */

/** Filter out groups that don't match the given name. */
public class NameSelector extends GroupSelector 
{
  String targetName;

  public NameSelector(String name) {
    this.targetName = name;
  }

  public void process(int group) {
    String groupName = counts.name(group);
    if (!targetName.equals(groupName))
      return;
    next.process(group);
  }

  public String toString() {
    return "name(" + targetName + ") -> " + next.toString();
  }
}

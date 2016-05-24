package net.dataninja.ee.textEngine.facet;


/*
net.dataninja copyright statement
 */

/** Select page sets of siblings around selected groups */
public class PageSelector extends GroupSelector 
{
  private int pageSize = 10;

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public void process(int group) 
  {
    int lastPageStart = -1;

    // In conservative mode, select everything.
    if (conservative) {
      next.process(group);
      return;
    }

    // Figure out which page this group is on among its siblings.
    int parent = counts.parent(group);
    int n = 0;
    for (int kid = counts.child(parent); kid >= 0; kid = counts.sibling(kid)) {
      if (!counts.shouldInclude(kid))
        continue;
      if ((n % pageSize) == 0)
        lastPageStart = kid;
      if (kid == group)
        break;
      ++n;
    }
    assert lastPageStart >= 0 : "incorrect tree data";

    // Now select everything on this page.
    n = 0;
    for (int kid = lastPageStart; kid >= 0; kid = counts.sibling(kid)) {
      next.process(kid);
      ++n;
      if (n == pageSize)
        break;
    }
  } // process()

  public String toString() {
    return "page(" + pageSize + ") -> " + next.toString();
  }
}

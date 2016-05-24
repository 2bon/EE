package net.dataninja.ee.textEngine.facet;


/*
net.dataninja copyright statement
 */

/** Select a range of the incoming groups */
public class RangeSelector extends GroupSelector 
{
  int start;
  int max;
  int skipped;
  int returned;

  public RangeSelector(int start, int max) {
    this.start = start;
    this.max = max;
  }

  public void reset(boolean conservative) 
  {
    super.reset(conservative);
    if (conservative) {
      if (counts.nondefaultSort())
        skipped = 999999999;
      else
        skipped = 0;
      returned = -999999999;
    }
    else
      skipped = returned = 0;
  }

  public void process(int group) 
  {
    if (skipped < start) {
      ++skipped;
      return;
    }
    if (returned < max) {
      ++returned;
      next.process(group);
    }
  }

  public String toString() {
    return "range(" + start + "," + max + ") -> " + next.toString();
  }
}

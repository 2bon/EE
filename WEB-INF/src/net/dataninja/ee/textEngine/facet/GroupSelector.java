package net.dataninja.ee.textEngine.facet;


/*
net.dataninja copyright statement
 */

/**
 * Base class for the various selector classes that are chained together
 * to execute a selection expression in a faceted query.
 *
 * @author Rick Li
 */
public abstract class GroupSelector 
{
  protected boolean conservative;
  protected GroupSelector next;
  protected GroupCounts counts;

  /** Set the next selector in the chain */
  public void setNext(GroupSelector next) {
    this.next = next;
  }

  /** Set the counts to be used */
  public void setCounts(GroupCounts counts) {
    this.counts = counts;
    if (next != null)
      next.setCounts(counts);
  }

  /** Reset the selector */
  public void reset(boolean conservative) {
    this.conservative = conservative;
    if (next != null)
      next.reset(conservative);
  }

  /** Process the next group */
  public abstract void process(int group);

  /** Flush any queued groups */
  public void flush() {
    if (next != null)
      next.flush();
  }

  /** Get a string representation */
  public abstract String toString();
} // class GroupSelector

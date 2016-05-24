package net.dataninja.ee.textEngine;


/*
dataninja copyright statement
 */
import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.limit.LimIndexReader;
import net.dataninja.ee.util.ThreadWatcher;

/**
 * Just like a {@link LimIndexReader} except it also does a periodic check if
 * the request has taken too long and should kill itself.
 */
public class XtfLimIndexReader extends LimIndexReader 
{
  private int killCheckCounter = 0;

  /** Construct the index reader */
  public XtfLimIndexReader(IndexReader toWrap, int workLimit) {
    super(toWrap, workLimit);
  } // constructor

  /**
   * Called by LimTermDocs and LimTermPositions to notify us that a certain
   * amount of work has been done. We check the limit, and if exceeded, throw
   * an exception.
   *
   * @param amount    How much work has been done. The unit is typically one
   *                  term or term-position.
   */
  protected final void work(int amount)
    throws IOException 
  {
    super.work(amount);

    // Every once in a while, check if our thread has exceeded its time
    // limit and should kill itself.
    //
    if (killCheckCounter++ > 1000) {
      killCheckCounter = 0;
      if (ThreadWatcher.shouldDie(Thread.currentThread()))
        throw new RuntimeException("Runaway request - time limit exceeded");
    }
  } // work()
} // class XtfLimIndexReader

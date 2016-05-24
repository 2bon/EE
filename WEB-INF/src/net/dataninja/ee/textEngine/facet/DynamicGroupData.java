package net.dataninja.ee.textEngine.facet;


/**
dataninja copyright statement
 */
import java.io.IOException;
import java.util.Set;

import org.apache.lucene.index.IndexReader;

/**
 * Abstract class representing dynamically generated group data for a facet
 * query.
 */
public abstract class DynamicGroupData extends GroupData 
{
  /**
   * Initialize the data from the given index reader, set of tokenized fields, 
   * and parameter string.
   */
  public abstract void init(IndexReader indexReader, Set tokFields, String params)
    throws IOException;

  /**
   * Collect/build data for the given document and score.
   * @param doc     Lucene document identifier for matching document
   * @param score   Calculated score for the doc (always greater than zero)
   */
  public abstract void collect(int doc, float score);

  /**
   * Complete any tasks to finish building the group data.
   */
  public abstract void finish();
} // class DynamicGroupData

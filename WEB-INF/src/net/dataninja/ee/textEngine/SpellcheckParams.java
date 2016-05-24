package net.dataninja.ee.textEngine;

import java.util.Set;

/*
dataninja copyright statement
 */

/**
 * Various parameters that affect spell-checking of query terms.
 */
public class SpellcheckParams 
{
  /**
   * Fields to scan in the query for possibly misspelled terms. If null,
   * all tokenized fields are considered.
   */
  public Set fields = null;

  /**
   * Document score cutoff. If any document's non-normalized score is higher
   * than this, no suggestions will be made.
   */
  public float docScoreCutoff = 0;

  /**
   * Total documents cutoff. If the query results in more document hits than
   * this, no suggestions will be made.
   */
  public int totalDocsCutoff = 10;
  
} // class SpellcheckParams

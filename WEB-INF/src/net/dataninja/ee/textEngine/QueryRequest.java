package net.dataninja.ee.textEngine;

import org.apache.lucene.mark.ContextMarker;
import org.apache.lucene.search.Query;
import net.dataninja.ee.textEngine.facet.FacetSpec;

/**
net.dataninja copyright statement
 */

/**
 * Stores a single query request to be processed by the ee text engine.
 *
 * @author Rick Li
 */
public class QueryRequest implements Cloneable 
{
  /** Path (base dir relative) for the resultFormatter stylesheet */
  public String displayStyle;

  /** Document rank to start with (0-based) */
  public int startDoc = 0;

  /** Max # documents to return from this query */
  public int maxDocs = 10;

  /** Path to the Lucene index we want to search */
  public String indexPath;

  /** The Lucene query to perform */
  public Query query;

  /** Optional list of fields to sort documents by */
  public String sortMetaFields;

  /** Target size, in characters, for snippets */
  public int maxContext = 80;

  /** Limit on the total number of terms allowed */
  public int termLimit = 50;

  /** Limit on the total amount of "work" */
  public int workLimit = 0;

  /** Term marking mode */
  public int termMode = ContextMarker.MARK_SPAN_TERMS;

  /** Facet specifications (if any) */
  public FacetSpec[] facetSpecs = null;

  /** Whether to normalize scores (turn off to help debug ranking problems) */
  public boolean normalizeScores = true;

  /**
   * Whether to calculate an explanation of each score. Time-consuming, so
   * should not be used except during development
   */
  public boolean explainScores = false;

  /** Experimental, and probably temporary: Boost set info */
  public BoostSetParams boostSetParams = null;

  /** Experimental: provide spelling suggestions */
  public SpellcheckParams spellcheckParams = null;

  /** Optional: the <parameters> block sent to the query parser stylesheet */
  public String parserInput = null;

  /** Optional: the raw output of the query parser stylesheet */
  public String parserOutput = null;

  /** Optional: list of metadata fields to return (defaults to all) */
  public String returnMetaFields = null;

  // Creates an exact copy of this query request.
  public Object clone() 
  {
    try {
      return super.clone();
    }
    catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  } // clone()
} // class QueryRequest

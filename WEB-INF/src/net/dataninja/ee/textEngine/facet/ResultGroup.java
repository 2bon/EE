package net.dataninja.ee.textEngine.facet;

import net.dataninja.ee.textEngine.DocHit;

/*
net.dataninja copyright statement
 */

/**
 * Records the results of a single group in field-grouped query.
 *
 * @author Rick Li
 */
public class ResultGroup 
{
  /** Facet value for this group */
  public String value;

  /** Ordinal rank of this group */
  public int rank;

  /**
   * Total number of sub-groups (possibly more than are selected by this
   * particular request.)
   */
  public int totalSubGroups;

  /** The selected sub-groups (if any) */
  public ResultGroup[] subGroups;

  /**
   * Total number of documents in this group (possibly many more than are
   * returned in this particular request.)
   */
  public int totalDocs;

  /** Ordinal rank of the first document hit returned (0-based) */
  public int startDoc;

  /** Oridinal rank of the last document hit returned, plus 1 */
  public int endDoc;

  /** One hit per document */
  public DocHit[] docHits;
} // class ResultGroup

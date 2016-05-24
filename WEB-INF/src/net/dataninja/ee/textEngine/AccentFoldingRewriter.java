package net.dataninja.ee.textEngine;


/*
dataninja copyright statement
 */
import java.util.Set;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.search.spans.SpanWildcardQuery;
import net.dataninja.ee.util.CharMap;

/*
 * This file created on Apr 15, 2005 by Rick Li
 */

/**
 * Rewrites a Lucene query to replace all accented words with the same
 * word minus diacritics.
 *
 * @author Rick Li
 */
public class AccentFoldingRewriter extends XtfQueryRewriter 
{
  private CharMap accentMap;
  private Set tokenizedFields;

  /** Construct a new rewriter to use the given map
   * @param tokFields */
  public AccentFoldingRewriter(CharMap accentMap, Set tokFields) {
    this.accentMap = accentMap;
    this.tokenizedFields = tokFields;
  }

  /**
   * Rewrite a term query. This is only called for artificial queries
   * introduced by ee system itself, and therefore we don't map here.
   */
  protected Query rewrite(TermQuery q) {
    return q;
  }

  /**
   * Rewrite a span term query. Removes diacritics from words.
   *
   * @param q  The query to rewrite
   * @return   Rewritten version, or 'q' unchanged if no changed needed.
   */
  protected Query rewrite(SpanTermQuery q) 
  {
    Term t = q.getTerm();
    if (!tokenizedFields.contains(t.field()))
      return q;

    String mapped = accentMap.mapWord(t.text());
    if (mapped == null)
      return q;

    Term newTerm = new Term(t.field(), mapped);
    return copyBoost(q, new SpanTermQuery(newTerm, q.getTermLength()));
  }

  /**
   * Rewrite a wildcard term query. Removes diacritics from words.
   *
   * @param q  The query to rewrite
   * @return   Rewritten version, or 'q' unchanged if no changed needed.
   */
  protected Query rewrite(SpanWildcardQuery q) 
  {
    assert q instanceof XtfSpanWildcardQuery;

    Term t = q.getTerm();
    if (!tokenizedFields.contains(t.field()))
      return q;

    String mapped = accentMap.mapWord(t.text());
    if (mapped == null)
      return q;

    Term newTerm = new Term(t.field(), mapped);
    return copyBoost(q, new XtfSpanWildcardQuery(newTerm, q.getTermLimit()));
  }
} // class AccentFoldingRewriter

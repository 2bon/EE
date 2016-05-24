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

/*
 * This file created on Jan 17, 2007 by Rick Li
 */

/**
 * Rewrites a Lucene query to perform standard tokenization actions on
 * each term, such as converting them to lowercase, removing apostrophes,
 * etc.
 *
 * @author Rick Li
 */
public class StdTermRewriter extends XtfQueryRewriter 
{
  private Set tokenizedFields;
  private StdTermFilter filter = new StdTermFilter();

  /**
   * Construct a term rewriter that will operate on the given tokenized
   * fields.
   */
  public StdTermRewriter(Set tokFields) {
    tokenizedFields = tokFields;
  }

  /**
   * Rewrite a term query. This is only called for artificial queries
   * introduced by ee system itself, and therefore we don't map here.
   */
  protected Query rewrite(TermQuery q) {
    return q;
  }

  /**
   * Rewrite a span term query.
   *
   * @param q  The query to rewrite
   * @return   Rewritten version, or 'q' unchanged if no changed needed.
   */
  protected Query rewrite(SpanTermQuery q) 
  {
    Term t = q.getTerm();
    String mapped = mapTerm(t);
    if (mapped == null)
      return q;

    Term newTerm = new Term(t.field(), mapped);
    return copyBoost(q, new SpanTermQuery(newTerm, q.getTermLength()));
  }

  /**
   * Rewrite a wildcard term query.
   *
   * @param q  The query to rewrite
   * @return   Rewritten version, or 'q' unchanged if no changed needed.
   */
  protected Query rewrite(SpanWildcardQuery q) 
  {
    assert q instanceof XtfSpanWildcardQuery;

    Term t = q.getTerm();
    String mapped = mapTerm(t);
    if (mapped == null)
      return q;

    Term newTerm = new Term(t.field(), mapped);
    return copyBoost(q, new XtfSpanWildcardQuery(newTerm, q.getTermLimit()));
  }

  /**
   * Map the given term and return the mapped result.
   *
   * @param t   term to map
   * @return    different term, or null if not different. Also null if the
   *            field isn't tokenized.
   */
  private String mapTerm(Term t) 
  {
    // If the field isn't tokenized, leave the term unmodified.
    if (!tokenizedFields.contains(t.field()))
      return null;

    // Okay, let's try mapping it.
    String mapped = filter.filter(t.text());
    if (mapped.equals(t.text()))
      return null;
    return mapped;
  }
} // class

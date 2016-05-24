package net.dataninja.ee.textEngine;


/*
dataninja copyright statement
 */
import java.util.Set;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import net.dataninja.ee.util.WordMap;

/*
 * This file created on Apr 15, 2005 by Rick Li
 */

/**
 * Rewrites a Lucene query to replace all plural words with their singular
 * equivalents.
 *
 * @author Rick Li
 */
public class PluralFoldingRewriter extends XtfQueryRewriter 
{
  private WordMap pluralMap;
  private Set tokenizedFields;

  /** Construct a new rewriter to use the given map  */
  public PluralFoldingRewriter(WordMap pluralMap, Set tokFields) {
    this.pluralMap = pluralMap;
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
   * Rewrite a span term query. Maps plural words to singular, but only
   * for tokenized fields.
   *
   * @param q  The query to rewrite
   * @return   Rewritten version, or 'q' unchanged if no changed needed.
   */
  protected Query rewrite(SpanTermQuery q) 
  {
    Term t = q.getTerm();
    if (!tokenizedFields.contains(t.field()))
      return q;

    String mapped = pluralMap.lookup(t.text());
    if (mapped == null)
      return q;

    Term newTerm = new Term(t.field(), mapped);
    return copyBoost(q, new SpanTermQuery(newTerm, q.getTermLength()));
  }
} // class PluralFoldingRewriter

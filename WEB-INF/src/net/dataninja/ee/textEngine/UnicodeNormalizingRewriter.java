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
import net.dataninja.ee.util.FastCache;
import net.dataninja.ee.util.Normalizer;

/*
 * This file created on Apr 15, 2005 by Rick Li
 */

/**
 * Rewrites a Lucene query to replace all non-normalized words
 * (i.e. not encoded in Normalized-Form-C) with normalized ones.
 * For instance, many diacritics actually need to be combined with
 * their main letter rather than as separate combining marks.
 *
 * @author Rick Li
 */
public class UnicodeNormalizingRewriter extends XtfQueryRewriter 
{
  /** How many recent mappings to maintain */
  private static final int CACHE_SIZE = 5000;

  /** Keep a cache of lookups performed to-date */
  private FastCache<String, String> cache = new FastCache(CACHE_SIZE);

  /** Set of fields that are tokenized in the index */
  private Set tokenizedFields;

  /** Construct a new rewriter. Will only operate on tokenized fields. */
  public UnicodeNormalizingRewriter(Set tokFields) {
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
   * Rewrite a span term query. Normalizes Unicode to NFC.
   *
   * @param q  The query to rewrite
   * @return   Rewritten version, or 'q' unchanged if no changed needed.
   */
  protected Query rewrite(SpanTermQuery q) 
  {
    Term t = q.getTerm();
    if (!tokenizedFields.contains(t.field()))
      return q;

    // Only do the (sometimes lengthy) normalization step if we haven't already 
    // looked up this token.
    //
    String text = t.text();
    if (!cache.contains(text)) {
      String normalizedText = Normalizer.normalize(text);
      cache.put(text, normalizedText);
    }
    String newText = cache.get(text);
    if (newText.equals(text))
      return q;
    
    Term newTerm = new Term(t.field(), newText);
    return copyBoost(q, new SpanTermQuery(newTerm, q.getTermLength()));
  }

  /**
   * Rewrite a wildcard term query. Normalizes Unicode encoding to NFC in all words.
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

    // Only do the (sometimes lengthy) normalization step if we haven't already 
    // looked up this token.
    //
    String text = t.text();
    if (!cache.contains(text)) {
      String normalizedText = Normalizer.normalize(text);
      cache.put(text, normalizedText);
    }
    String newText = cache.get(text);
    if (newText.equals(text))
      return q;
    
    Term newTerm = new Term(t.field(), newText);
    return copyBoost(q, new XtfSpanWildcardQuery(newTerm, q.getTermLimit()));
  }
} // class UnicodeNormalizingRewriter

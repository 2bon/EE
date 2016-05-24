package net.dataninja.ee.textEngine;


/**
dataninja copyright statement
 */
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.spans.SpanRangeQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.search.spans.SpanWildcardQuery;

/**
 * This class swaps the current field of every sub-query to the specified
 * field.
 *
 * @author Rick Li
 */
public class RefieldingQueryRewriter extends XtfQueryRewriter 
{
  private String newField;

  /** Change the field name of the given query */
  public synchronized Query refield(Query q, String field) {
    newField = field;
    return rewriteQuery(q);
  }

  /** Switch the field of the given term */
  private Term rewriteTerm(Term t) {
    if (t == null)
      return t;
    return new Term(newField, t.text());
  }

  // inherit JavaDoc
  protected Query rewrite(TermQuery q) {
    return new TermQuery(rewriteTerm(q.getTerm()));
  }

  // inherit JavaDoc
  protected Query rewrite(SpanTermQuery q) {
    return new SpanTermQuery(rewriteTerm(q.getTerm()));
  }

  // inherit JavaDoc
  protected Query rewrite(SpanWildcardQuery q) {
    assert q instanceof XtfSpanWildcardQuery;
    return new XtfSpanWildcardQuery(rewriteTerm(q.getTerm()), q.getTermLimit());
  }

  // inherit JavaDoc
  protected Query rewrite(SpanRangeQuery q) {
    assert q instanceof XtfSpanRangeQuery;
    return new XtfSpanRangeQuery(rewriteTerm(q.getLowerTerm()),
                                 rewriteTerm(q.getUpperTerm()),
                                 q.isInclusive(),
                                 q.getTermLimit());
  }

  // inherit JavaDoc
  protected Query rewrite(NumericRangeQuery nrq) {
    return new NumericRangeQuery(newField,
                                 nrq.getLowerVal(),
                                 nrq.getUpperVal(),
                                 nrq.includesLower(),
                                 nrq.includesUpper());
  }
} // class FieldSwappingQueryRewriter

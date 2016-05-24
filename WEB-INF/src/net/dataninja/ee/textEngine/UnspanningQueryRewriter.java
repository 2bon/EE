package net.dataninja.ee.textEngine;

/**
dataninja copyright statement
 */

import java.util.Stack;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.spans.SpanOrQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;

/**
 * This class converts some common span queries to their faster,
 * non-span equivalents.
 *
 * @author Rick Li
 */
public class UnspanningQueryRewriter extends XtfQueryRewriter 
{
  private Stack parentStack = new Stack();

  public Query rewriteQuery(Query q) 
  {
    try {
      parentStack.push(q);
      return super.rewriteQuery(q);
    }
    finally {
      parentStack.pop();
    }
  }

  /**
   * For span queries with children, we don't want to un-span those children
   * because the span queries would then break.
   */
  private boolean suppressRewrite() 
  {
    for (int i = 0; i < parentStack.size() - 1; i++) {
      if (parentStack.get(i) instanceof SpanQuery)
        return true;
    }
    return false;
  }

  /**
   * Replace span term queries, if they're not children of another span
   * query, with normal term queries.
   */
  protected Query rewrite(SpanTermQuery q) {
    if (suppressRewrite())
      return q;
    return new TermQuery(q.getTerm());
  }

  /**
   * Replace span OR queries with more efficient plain OR, unless the parent
   * query is another span query.
   */
  protected Query rewrite(SpanOrQuery oq) 
  {
    if (suppressRewrite())
      return oq;

    // Rewrite each term, and add to a plain boolean query.
    BooleanQuery newQuery = new BooleanQuery();

    SpanQuery[] clauses = oq.getClauses();
    for (int i = 0; i < clauses.length; i++)
      newQuery.add(rewriteQuery(clauses[i]), BooleanClause.Occur.SHOULD);

    // Retain the original boost, if any.
    return copyBoost(oq, newQuery);
  }
} // class FieldSwappingQueryRewriter

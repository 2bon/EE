package net.dataninja.ee.textEngine;


/*
dataninja copyright statement
 */
import java.util.Set;
import org.apache.lucene.chunk.DocNumMap;
import org.apache.lucene.chunk.SpanChunkedNotQuery;
import org.apache.lucene.chunk.SpanDechunkingQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanOrNearQuery;
import org.apache.lucene.search.spans.SpanRangeQuery;
import org.apache.lucene.search.spans.SpanWildcardQuery;
import net.dataninja.ee.util.CharMap;
import net.dataninja.ee.util.WordMap;

/**
 * Fix up all the "infinite" slop entries to be actually limited to
 * the chunk overlap size. That way, we'll get consistent results and
 * the user won't be able to tell where the chunk boundaries are.
 *
 * Also attaches a DocNumMap to each SpanDechunkingQuery.
 *
 * @author Rick Li
 */
public class SlopFixupRewriter extends XtfQueryRewriter 
{
  private DocNumMap docNumMap;
  private Set stopSet;
  private WordMap pluralMap;
  private CharMap accentMap;

  /** Construct a new rewriter */
  public SlopFixupRewriter(DocNumMap docNumMap, Set stopSet, WordMap pluralMap,
                           CharMap accentMap) 
  {
    this.docNumMap = docNumMap;
    this.stopSet = stopSet;
    this.pluralMap = pluralMap;
    this.accentMap = accentMap;
  }

  public boolean forceRewrite(Query q) {
    return (q instanceof SpanNearQuery) || (q instanceof SpanOrNearQuery) ||
           (q instanceof SpanChunkedNotQuery) ||
           (q instanceof SpanDechunkingQuery) ||
           (q instanceof SpanWildcardQuery) || (q instanceof SpanRangeQuery) ||
           (q instanceof MoreLikeThisQuery);
  }

  public Query rewrite(SpanNearQuery nq) 
  {
    // For text queries, set the max to the chunk overlap size. For
    // meta-data fields, set it to the bump between multiple values
    // for the same field, *minus one* to prevent matches across 
    // the boundary.
    //
    boolean isText = nq.getField().equals("text");
    int maxSlop = isText ? docNumMap.getChunkOverlap() : (1000000 - 1);
    int targetSlop = Math.min(nq.getSlop(), maxSlop);
    if (targetSlop == nq.getSlop())
      return super.rewrite(nq);

    // Okay, rewrite and reset the slop.
    SpanNearQuery newQ = (SpanNearQuery)super.rewrite(nq);
    assert newQ != nq;
    newQ.setSlop(targetSlop);
    return newQ;
  }

  public Query rewrite(SpanOrNearQuery nq) 
  {
    // For text queries, set the max to the chunk overlap size. For
    // meta-data fields, set it to the bump between multiple values
    // for the same field, *minus one* to prevent matches across 
    // the boundary.
    //
    boolean isText = nq.getField().equals("text");
    int maxSlop = isText ? docNumMap.getChunkOverlap() : (1000000 - 1);
    int targetSlop = Math.min(nq.getSlop(), maxSlop);
    if (targetSlop == nq.getSlop())
      return super.rewrite(nq);

    // Okay, rewrite and reset the slop.
    SpanOrNearQuery newQ = (SpanOrNearQuery)super.rewrite(nq);
    assert newQ != nq;
    newQ.setSlop(targetSlop);
    return newQ;
  }

  public Query rewrite(SpanChunkedNotQuery nq) 
  {
    SpanChunkedNotQuery newq = (SpanChunkedNotQuery)super.rewrite(nq);
    assert newq != nq;

    // Properly limit the slop.
    newq.setSlop(Math.min(nq.getSlop(), docNumMap.getChunkOverlap()),
                 docNumMap.getChunkSize() - docNumMap.getChunkOverlap());
    return newq;
  }

  public Query rewrite(SpanDechunkingQuery q) {
    SpanDechunkingQuery newq = (SpanDechunkingQuery)super.rewrite(q);
    assert newq != q;
    newq.setDocNumMap(docNumMap);
    return newq;
  }

  public Query rewrite(SpanWildcardQuery q) {
    assert q instanceof XtfSpanWildcardQuery;
    XtfSpanWildcardQuery newq = (XtfSpanWildcardQuery)super.rewrite(q);
    assert newq != q;
    newq.setStopWords(stopSet);
    return newq;
  }

  public Query rewrite(SpanRangeQuery q) {
    assert q instanceof XtfSpanRangeQuery;
    XtfSpanRangeQuery newq = (XtfSpanRangeQuery)super.rewrite(q);
    assert newq != q;
    newq.setStopWords(stopSet);
    return newq;
  }

  public Query rewrite(MoreLikeThisQuery q) {
    MoreLikeThisQuery newq = (MoreLikeThisQuery)super.rewrite(q);
    assert newq != q;
    newq.setStopWords(stopSet);
    newq.setPluralMap(pluralMap);
    newq.setAccentMap(accentMap);
    return newq;
  }
} // class SlopFixupRewriter

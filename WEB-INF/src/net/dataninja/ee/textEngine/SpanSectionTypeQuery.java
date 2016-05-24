package net.dataninja.ee.textEngine;


/**
net.dataninja copyright statement
 */
import java.io.IOException;
import java.util.Collection;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.Spans;

/**
 * Supports sectionType filtering of text chunks. Spans from the text
 * query are filtered out if they don't match the sectionType document
 * numbers.
 *
 * @author Rick Li
 */
public class SpanSectionTypeQuery extends SpanQuery 
{
  /** Text query to filter */
  private SpanQuery textQuery;

  /** Query on the 'sectionType' field, used to limit text query results */
  private SpanQuery typeQuery;

  /**
   * Construct a filtered query.
   *
   * @param textQuery         Text query to filter
   * @param sectionTypeQuery  'sectionType' field query to filter with
   */
  public SpanSectionTypeQuery(SpanQuery textQuery, SpanQuery sectionTypeQuery) 
  {
    // Record the input parms
    this.typeQuery = sectionTypeQuery;
    this.textQuery = textQuery;
  } // constructor

  public SpanQuery getTextQuery() {
    return textQuery;
  }

  public SpanQuery getSectionTypeQuery() {
    return typeQuery;
  }

  // inherit javadoc
  public Query rewrite(IndexReader reader)
    throws IOException 
  {
    SpanQuery rewrittenText = (SpanQuery)textQuery.rewrite(reader);
    SpanQuery rewrittenType = (SpanQuery)typeQuery.rewrite(reader);
    if (rewrittenText == textQuery && rewrittenType == typeQuery)
      return this;
    SpanSectionTypeQuery clone = (SpanSectionTypeQuery)this.clone();
    clone.textQuery = rewrittenText;
    clone.typeQuery = rewrittenType;
    return clone;
  }

  /**
   * Iterate all the spans from the text query that match the sectionType
   * query also.
   */
  public Spans getSpans(final IndexReader reader, final Searcher searcher)
    throws IOException 
  {
    return new Spans() 
    {
      private Spans typeSpans = typeQuery.getSpans(reader, searcher);
      private boolean moreType = true;
      private Spans textSpans = textQuery.getSpans(reader, searcher);
      private boolean moreText = true;
      private boolean firstTime = true;

      public boolean next()
        throws IOException 
      {
        if (moreText) // move to next text
          moreText = textSpans.next();
        if (firstTime) {
          moreType = typeSpans.next();
          firstTime = false;
        }

        return advance();
      }

      public boolean skipTo(int target)
        throws IOException 
      {
        moreText = textSpans.skipTo(target);
        moreType = typeSpans.skipTo(target);
        return advance();
      }

      private boolean advance()
        throws IOException 
      {
        while (moreText && moreType) 
        {
          // Advance text to type, or type to text. Note that the type
          // query MUST support skipTo(), that is, it must return documents
          // in order. Otherwise, the logic below totally breaks. Note that
          // BooleanQuery, in particular, does not meet this criterion.
          //
          final int textChunk = textSpans.doc();
          final int typeChunk = typeSpans.doc();

          if (textChunk < typeChunk)
            moreText = textSpans.skipTo(typeChunk);
          else if (textChunk > typeChunk)
            moreType = typeSpans.skipTo(textChunk);
          else
            break;
        }

        return moreText && moreType;
      }

      public int doc() {
        return textSpans.doc();
      }

      public int start() {
        return textSpans.start();
      }

      public int end() {
        return textSpans.end();
      }

      public float score() {
        return textSpans.score() * getBoost();
      }

      public String toString() {
        return textSpans.toString();
      }

      public Explanation explain()
        throws IOException 
      {
        if (getBoost() == 1.0f)
          return textSpans.explain();

        Explanation result = new Explanation(0,
                                             "weight(" + toString() +
                                             "), product of:");

        Explanation boostExpl = new Explanation(getBoost(), "boost");
        result.addDetail(boostExpl);

        Explanation inclExpl = textSpans.explain();
        result.addDetail(inclExpl);

        result.setValue(boostExpl.getValue() * inclExpl.getValue());
        return result;
      }
    };
  }

  public String getField() {
    return textQuery.getField();
  }

  public Collection getTerms() {
    return textQuery.getTerms();
  }

  public String toString(String field) {
    StringBuffer buffer = new StringBuffer();
    buffer.append("spanSectionType(");
    buffer.append(textQuery.toString(field));
    buffer.append(",");
    buffer.append(typeQuery.toString(field));
    buffer.append(")");
    return buffer.toString();
  }
} // class SpanSectionTypeFilterQuery

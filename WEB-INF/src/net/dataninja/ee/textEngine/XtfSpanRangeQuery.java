package net.dataninja.ee.textEngine;


/*
dataninja copyright statement
 */
import org.apache.lucene.bigram.BigramSpanRangeQuery;
import org.apache.lucene.index.Term;

/**
 * Matches spans containing terms within a specified range. Performs extra
 * filtering to make sure bi-grams are not matched, and that start/end of
 * field marks are not matched.
 */
public class XtfSpanRangeQuery extends BigramSpanRangeQuery 
{
  public XtfSpanRangeQuery(Term lowerTerm, Term upperTerm, boolean inclusive,
                           int termLimit) 
  {
    super(lowerTerm, upperTerm, inclusive, termLimit);
  }

  protected boolean shouldSkipTerm(Term term) 
  {
    // Skip the special start-of-field and end-of-field terms.
    String word = term.text();
    if (word.length() > 1) {
      if (word.charAt(0) == Constants.FIELD_START_MARKER)
        return true;
      if (word.charAt(word.length() - 1) == Constants.FIELD_END_MARKER)
        return true;
    }

    // Do the normal thing.
    return super.shouldSkipTerm(term);
  }
} // class XtfSpanRangeQuery

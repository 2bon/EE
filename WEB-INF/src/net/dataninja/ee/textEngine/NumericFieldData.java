package net.dataninja.ee.textEngine;


/**
dataninja copyright statement
 */
import java.io.IOException;
import java.util.HashMap;
import java.util.WeakHashMap;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.util.IntList;
import org.apache.lucene.util.LongList;

/**
 * Holds numeric data for a field from a Lucene index. Data is cached for a
 * given index reader, to speed access after the initial load.
 *
 * @author Rick Li
 */
public class NumericFieldData 
{
  /** Cached data. If the reader goes away, our cache will too. */
  private static WeakHashMap cache = new WeakHashMap();

  /** Document IDs containing values for the field */
  private IntList docs = new IntList();

  /** Associated numeric value for each document */
  private LongList values = new LongList();

  /**
   * Retrieves tags for a given field from a given reader. Maintains a cache
   * so that if the same fields are requested again for this reader, we don't have
   * to re-read the tags.
   *
   * @param reader  Where to read the tags from
   * @param field   Which field to read
   * @return        FRBR tags for the specified field
   */
  public static NumericFieldData getCachedData(IndexReader reader, String field)
    throws IOException 
  {
    // See if we have a cache for this reader.
    HashMap readerCache = (HashMap)cache.get(reader);
    if (readerCache == null) {
      readerCache = new HashMap();
      cache.put(reader, readerCache);
    }

    // Now see if we've already read data for this field.
    NumericFieldData data = (NumericFieldData)readerCache.get(field);
    if (data == null) 
    {
      // Don't have cached data, so read and remember it.
      data = new NumericFieldData(reader, field);
      readerCache.put(field, data);
    }

    return data;
  } // getCachedTags()

  /** Parse the numeric characters of a string, ignoring all non-digits */
  public static long parseVal(String str) 
  {
    long ret = 0;
    for (int i = 0; i < str.length(); i++) 
    {
      int digit = Character.digit(str.charAt(i), 10);
      if (digit >= 0) {
        ret = (ret * 10) + digit;
      }
    }
    return ret;
  }

  /**
   * Load data from the given field of the reader, and parse the values as
   * numbers.
   */
  private NumericFieldData(IndexReader reader, String field)
    throws IOException 
  {
    TermDocs termDocs = reader.termDocs();
    TermEnum termEnum = reader.terms(new Term(field, ""));

    try 
    {
      // First, collect all the doc/value pairs.
      if (termEnum.term() == null)
        throw new IOException("no terms in field " + field);

      do 
      {
        Term term = termEnum.term();
        if (term.field() != field)
          break;

        String termText = term.text();

        // Skip terms with the special ee field markers.
        if (termText.length() > 1) {
          if (termText.charAt(0) == Constants.FIELD_START_MARKER)
            continue;
          if (termText.charAt(termText.length() - 1) == Constants.FIELD_END_MARKER)
            continue;
        }

        long value = parseVal(termText);

        termDocs.seek(termEnum);
        while (termDocs.next()) {
          int doc = termDocs.doc();
          docs.add(doc);
          values.add(value);
        }
      } while (termEnum.next());

      // Save space.
      docs.compact();
      values.compact();

      // Now sort by document ID, and apply the same ordering to the values,
      // to keep them in sync.
      //
      int[] map = docs.calcSortMap();
      docs.remap(map);
      values.remap(map);

      // Check to be sure no documents have multiple values.
      for (int i = 1; i < docs.size(); i++) 
      {
        if (docs.get(i - 1) == docs.get(i)) {
          throw new IOException(
            "A document contains more than one value in numeric field '" +
            field + "': values " + values.get(i - 1) + " and " + values.get(i));
        }
      } // for
    } // try
    finally {
      termEnum.close();
      termDocs.close();
    }
  } // constructor

  public final int size() {
    return docs.size();
  }

  public final int doc(int index) {
    return docs.get(index);
  }

  public final long value(int index) {
    return values.get(index);
  }

  public final int findDocIndex(int docId) {
    int idx = docs.binarySearch(docId);
    if (idx >= 0)
      return idx;
    else
      return -idx - 1; // from -ins - 1
  }

  public final int docPos(int docId) {
    return docs.binarySearch(docId);
  }
} // class NumericFieldData

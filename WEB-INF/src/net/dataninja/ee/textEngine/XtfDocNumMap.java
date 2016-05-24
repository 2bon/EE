package net.dataninja.ee.textEngine;


/**
net.dataninja copyright statement
 */
import java.io.IOException;
import org.apache.lucene.chunk.DocNumMap;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;

/**
 * Used to map chunk indexes to the corresponding document index, and
 * vice-versa. Only performs the load when necessary (typically dynaXML uses
 * the DocNumMap, while crossQuery doesn't.)
 *
 * @author Rick Li
 */
public class XtfDocNumMap implements DocNumMap 
{
  /** Where to get the data from */
  private IndexReader reader;

  /** Max number of words in a chunk */
  private int chunkSize;

  /** Number of words one chunk overlaps with the next */
  private int chunkOverlap;

  /** Total number of docInfo chunks found */
  private int nDocs;

  /** Array of indexes, one for each docInfo chunk */
  private int[] docNums = null; /* null until load() called */

  /** Caches result of previous scan, used for speed */
  private int prevNum = -1;

  /** Used in binary searching */
  private int low = -1;

  /** Used in binary searching */
  private int high = -1;

  /**
   * Make a map for the given reader. This reads in all the docInfo chunks
   * to determine the range of text chunks for each document.
   */
  public XtfDocNumMap(IndexReader reader, int chunkSize, int chunkOverlap)
    throws IOException 
  {
    this.reader = reader;
    this.chunkSize = chunkSize;
    this.chunkOverlap = chunkOverlap;
  } // constructor

  private synchronized void load() 
  {
    // If already loaded, don't do it again.
    if (docNums != null)
      return;

    try 
    {
      // Figure out how many entries we'll have, and make our array 
      // that big.
      //
      Term term = new Term("docInfo", "1");
      nDocs = reader.docFreq(term);
      docNums = new int[nDocs];

      // Get a list of all the "header" chunks for documents in this
      // index (i.e., documents with a "docInfo" field.)
      //
      TermDocs docHeaders = reader.termDocs(term);

      // Record each document number.
      int i = 0;
      while (docHeaders.next())
        docNums[i++] = docHeaders.doc();
      nDocs = i; // Account for possibly deleted docs
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /** Get the max number of words per chunk */
  public int getChunkSize() {
    return chunkSize;
  }

  /** Get the number of words one chunk overlaps with the next */
  public int getChunkOverlap() {
    return chunkOverlap;
  }

  /**
   * Return a count of the number of documents (not chunks) in the index.
   */
  public final int getDocCount() {
    return nDocs;
  }

  /**
   * Given a chunk number, return the corresponding document number that it
   * is part of. Note that like all Lucene indexes, this is ephemeral and
   * only applies to the given reader. If not found, returns -1; this can
   * basically only happen if the chunk number is greater than all document
   * numbers.
   *
   * @param chunkNumber Chunk number to translate
   * @return Document index, or -1 if no match.
   */
  public final synchronized int getDocNum(int chunkNumber) 
  {
    // Do a binary search for the chunk
    scan(chunkNumber);

    // Return the upper end, since the document info is written after
    // all of its chunks.
    //
    if (high == nDocs)
      return -1;
    return docNums[high];
  } // getDocNum()

  /**
   * Given a document number, this method returns the number of its first
   * chunk.
   */
  public final synchronized int getFirstChunk(int docNum) 
  {
    // Scan for the document
    scan(docNum);

    // If not found, get out.
    if (low < 0 || docNums[low] != docNum)
      return -1;

    if (low == 0)
      return 1; // Account for index info chunk
    else
      return docNums[low - 1] + 1;
  } // getFirstchunk()

  /**
   * Given a document number, this method returns the number of its last
   * chunk.
   */
  public final int getLastChunk(int docNum) {
    return docNum - 1;
  }

  /**
   * Perform a binary search looking for the given number. On exit, the
   * 'low' and 'high' member variables will be indexes into the array that
   * bracket the value.
   *
   * @param num   The number to look for.
   */
  private void scan(int num) 
  {
    // Early-out
    if (num == prevNum)
      return;

    // Make sure we load the data the first time. We do this lazily because
    // some indexes are only used for crossQuery, which doesn't really use
    // the info in a DocNumMap.
    //
    load();

    // Perform a simple binary search.
    int high = nDocs;

    // Perform a simple binary search.
    int low = -1;

    // Perform a simple binary search.
    int probe;
    while (high - low > 1) {
      probe = (high + low) / 2;
      if (docNums[probe] > num)
        high = probe;
      else
        low = probe;
    }

    // At this point, low and high bracket the value searched for.
    assert low == -1 || docNums[low] <= num;
    assert high == nDocs || docNums[high] > num;

    this.low = low;
    this.high = high;
  } // scan()
} // class DocNumMap

package net.dataninja.ee.textEngine;


/**
net.dataninja copyright statement
 */
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.FieldDoc;
import net.dataninja.ee.util.AttribList;

/**
 * Represents a query hit at the document level. May contain {@link Snippet}s
 * if those were requested.
 *
 * @author Rick Li
 */
public abstract class DocHit extends FieldDoc 
{
  /**
   * Construct a document hit. Package-private because these should only
   * be constructed inside the text engine.
   *
   * @param docNum    Lucene ID for the document info chunk
   * @param score     Score for this hit
   */
  DocHit(int docNum, float score) {
    super(docNum, score);
  }

  /**
   * Retrieve the original file path as recorded in the index (if any.)
   */
  public abstract String filePath();

  /**
   * Retrieve this document's record number within the main file, or zero
   * if this is the only record.
   */
  public abstract int recordNum();
  
  /**
   * Retrieve this document's subdocument name, or null if there is
   * no subdocument (the default).
   */
  public abstract String subDocument();

  /**
   * Retrieve a list of all meta-data name/value pairs associated with this
   * document.
   */
  public abstract AttribList metaData();

  /** Return the total number of snippets found for this document (not the
   *  number actually returned, which is limited by the max # of snippets
   *  specified in the query.)
   */
  public abstract int totalSnippets();

  /**
   * Return the number of snippets available (limited by the max # specified
   * in the original query.)
   */
  public abstract int nSnippets();

  /**
   * Retrieve the specified snippet. In general, crossQuery will set getText
   * to 'true', while dynaXML may set it either way, depending on whether
   * the document result formatter stylesheet references the &lt;snippet&gt;
   * elements in the SearchTree. It's always safe, but not quite as
   * efficient, to assume 'true'.
   *
   * @param hitNum    0..nSnippets()
   * @param getText   true to fetch the snippet text in context, false to
   *                  optionally skip that work and only fetch the rank,
   *                  score, etc.
   *
   */
  public abstract Snippet snippet(int hitNum, boolean getText);

  /**
   * Get an explanation of this document's score. Only available if
   * requested at query time.
   */
  public Explanation explanation() {
    return null;
  }
} // class DocHit

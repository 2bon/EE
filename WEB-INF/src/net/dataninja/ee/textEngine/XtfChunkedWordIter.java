package net.dataninja.ee.textEngine;


/*
net.dataninja copyright statement
 */
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.chunk.ChunkedWordIter;
import org.apache.lucene.chunk.DocNumMap;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.mark.MarkPos;
import org.apache.lucene.mark.WordIter;

/**
 * Handles iterating over ee's tokenized documents, including special
 * tracking of node numbers and word offsets.
 *
 * @author Rick Li
 */
public class XtfChunkedWordIter extends ChunkedWordIter 
{
  /**
   * Construct the iterator and read in starting text from the given
   * chunk.
   *
   * @param reader      where to read chunks from
   * @param docNumMap   maps main doc num to chunk numbers
   * @param mainDocNum  doc ID of the main document
   * @param field       field tokenize and iterate
   * @param analyzer    used to tokenize the field
   */
  public XtfChunkedWordIter(IndexReader reader, DocNumMap docNumMap,
                            int mainDocNum, String field, Analyzer analyzer) 
  {
    super(new XtfChunkSource(reader, docNumMap, mainDocNum, field, analyzer));
  } // constructor

  /** Create an uninitialized MarkPos structure */
  public MarkPos getPos(int startOrEnd) {
    MarkPos pos = new XtfChunkMarkPos();
    getPos(pos, startOrEnd);
    return pos;
  }

  /** Get the position of the start of the current word */
  public void getPos(MarkPos pos, int startOrEnd) 
  {
    super.getPos(pos, startOrEnd);

    XtfChunkMarkPos xPos = (XtfChunkMarkPos)pos;

    if (startOrEnd == WordIter.TERM_END_PLUS)
      xPos.trim();

    if (chunk != null) {
      XtfChunk xc = (XtfChunk)chunk;
      xPos.nodeNumber = xc.nodeNumbers[tokNum];
      xPos.wordOffset = xc.wordOffsets[tokNum];
      xPos.sectionType = xc.sectionType;
    }
  }
} // class XtfChunkedWordIter

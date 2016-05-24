package net.dataninja.ee.textEngine;


/*
net.dataninja copyright statement
 */
import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.mark.BasicWordIter;
import org.apache.lucene.mark.MarkPos;
import org.apache.lucene.mark.WordIter;

/**
 * Just like a BasicWordIter, except that it enforces "soft" boundaries if
 * the source text contains ee "bump markers" of a certain size. Basically,
 * this prevents snippets from spanning section boundaries, or the boundaries
 * between different fields of the same name.
 *
 * @author Rick Li
 */
class BoundedWordIter extends BasicWordIter 
{
  int boundSize;

  /**
   * Construct a bounded word iterator on the given text. The tokens from
   * the stream must refer to the same text. The skip() method works as
   * normal, but next() and prev() will enforce a soft boundary for any
   * tokens where the position offset meets or exceeds boundSize.
   */
  public BoundedWordIter(String text, TokenStream stream, int boundSize)
    throws IOException 
  {
    super(text, stream);
    this.boundSize = boundSize;
  } // constructor

  /**
   * Advance to the next token.
   *
   * @return true if ok, false if no more.
   */
  public final boolean next(boolean force) 
  {
    if (force)
      return super.next(force);

    // Don't advance past separation in field value
    if (tokNum < tokens.length - 1 &&
        tokens[tokNum + 1].getPositionIncrement() >= boundSize) 
    {
      return false;
    }

    // Don't advance past 'end-of-field' token
    int offset = tokens[tokNum].endOffset();
    if (offset < text.length() &&
        text.charAt(offset) == Constants.FIELD_END_MARKER)
      return false;

    return super.next(force);
  } // next()

  /**
   * Go to the previous token.
   *
   * @return true if ok, false if no more.
   */
  public final boolean prev(boolean force) 
  {
    if (force)
      return super.prev(force);

    // Don't back past separation in field value
    if (tokens[tokNum].getPositionIncrement() >= boundSize)
      return false;

    // Don't back past 'start-of-field' token
    int offset = tokens[tokNum].startOffset();
    if (offset > 0 && text.charAt(offset - 1) == Constants.FIELD_START_MARKER)
      return false;

    return super.prev(force);
  } // prev()

  /** Create a new place to hold position info */
  public MarkPos getPos(int startOrEnd) {
    BoundedMarkPos pos = new BoundedMarkPos(tokens);
    getPos(pos, startOrEnd);
    return pos;
  }

  /**
   * Get the position of the end of the current word.
   */
  public void getPos(MarkPos pos, int startOrEnd) 
  {
    super.getPos(pos, startOrEnd);

    switch (startOrEnd) 
    {
      case WordIter.FIELD_START:
        ((BoundedMarkPos)pos).setTokNum(0);
        break;
      case WordIter.FIELD_END:
        ((BoundedMarkPos)pos).setTokNum(tokens.length - 1);
        break;
      case WordIter.TERM_END_PLUS:
        if (startOrEnd == WordIter.TERM_END_PLUS)
          ((BoundedMarkPos)pos).stripMarkers(tokens[tokNum].endOffset());

      // fall through...
      default:
        ((BoundedMarkPos)pos).setTokNum(tokNum);
    } // switch
  } // recordPos()
} // class BoundedWordIter

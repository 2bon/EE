package net.dataninja.ee.textEngine;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.mark.BasicMarkPos;
import org.apache.lucene.mark.MarkPos;

/*
net.dataninja copyright statement
 */

/**
 * Helps with marking fields that contain bump markers.
 *
 * @author Rick Li
 */
public class BoundedMarkPos extends BasicMarkPos 
{
  private Token[] tokens;
  private int tokNum;

  /** Creates a new mark pos */
  BoundedMarkPos(Token[] tokens) {
    this.tokens = tokens;
  }

  /** Establishes the token number of this mark pos */
  final void setTokNum(int tokNum) {
    this.tokNum = tokNum;
  }

  /**
   * Ensures that no XML elements or attributes are accidentally included in
   * the text. This is because, at the moment, we don't deal with all the
   * complexities of marking across XML tags (and it is very complex.)
   */
  public String getTextTo(MarkPos other, boolean checkUnmarkable) 
  {
    if (checkUnmarkable && other != null) 
    {
      // Check all the tokens between the two marks.
      for (int i = tokNum; i <= ((BoundedMarkPos)other).tokNum; i++) 
      {
        String term = tokens[i].termText();
        if (term.length() == 0)
          continue;
        if (term.charAt(0) == Constants.ELEMENT_MARKER ||
            term.charAt(0) == Constants.ATTRIBUTE_MARKER) 
        {
          throw new UnmarkableException();
        }
      } // for i
    } // if

    // Check passed... get the text.
    return super.getTextTo(other);
  }

  /**
   * Called by BoundedWordIter when called to get the END_PLUS of a token. We
   * strip off bump markers, whitespace, and end-of-field markers.
   */
  public void stripMarkers(int termEnd) 
  {
    // Remove bump markers.
    while (true) {
      int tmp = fullText.lastIndexOf(Constants.BUMP_MARKER, charPos - 1);
      if (tmp < termEnd)
        break;
      charPos = tmp;
    }

    // Remove trailing whitespace and end-of-field markers.
    for (; charPos > termEnd; charPos--) 
    {
      char c = fullText.charAt(charPos - 1);
      if (!Character.isWhitespace(fullText.charAt(charPos - 1)) &&
          c != Constants.FIELD_END_MARKER) 
      {
        break;
      }
    } // for
  } // stripMarkers()

  /** Exception thrown if asked to mark past XML elements or attributes */
  public static class UnmarkableException extends RuntimeException {
  }
} // class BoundedMarkPos()

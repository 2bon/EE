package net.dataninja.ee.textIndexer;


/*
net.dataninja copyright statement
 */
import java.io.IOException;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import net.dataninja.ee.util.CharMap;

/*
 * This file created on Apr 14, 2005 by Rick Li
 */

/**
 * Improves query results by converting accented characters to normal
 * characters by removing diacritics.
 *
 * @author Rick Li
 */
public class AccentFoldingFilter extends TokenFilter 
{
  /** Set of characters to map */
  private CharMap accentMap;

  /**
   * Construct a token stream to remove accents from the input tokens.
   *
   * @param input       Input stream of tokens to process
   * @param accentMap   Map of accented characters to their un-accented
   *                    counterparts.
   */
  public AccentFoldingFilter(TokenStream input, CharMap accentMap) 
  {
    // Initialize the super-class
    super(input);

    // Record the set of words to de-pluralize
    this.accentMap = accentMap;
  } // constructor

  /** Retrieve the next token in the stream. */
  public Token next()
    throws IOException 
  {
    int bumpAccum = 0;
    while (true)
    {
      // Get the next token. If we're at the end of the stream, get out.
      Token t = input.next();
      if (t == null)
        return t;
  
      // Does the word have any accented chars? If not, return it unchanged.
      String term = t.termText();
      String mapped = accentMap.mapWord(term);
      if (mapped == null) {
        if (bumpAccum == 0)
          return t;
        mapped = term; // We have accumulated bump to apply; must make new token.
      }
      
      // Special case: if the term is only combining marks or spaces, skip to the next
      // token. Be sure to retain any position bump though.
      //
      if (mapped.length() == 0) {
        bumpAccum += t.getPositionIncrement() - 1;
        continue;
      }
  
      // Okay, we gotta make a new token that's the same in every respect
      // except the word. If we skipped terms because they were only combining marks
      // or spaces, be sure to include their accumulated position increment.
      //
      Token newToken = new Token(mapped, t.startOffset(), t.endOffset(), t.type());
      newToken.setPositionIncrement(t.getPositionIncrement() + bumpAccum);
      return newToken;
    }
  } // next()
} // class AccentFoldingFilter

package net.dataninja.ee.textIndexer;


/*
net.dataninja copyright statement
 */
import java.io.IOException;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import net.dataninja.ee.textEngine.Constants;
import net.dataninja.ee.util.WordMap;

/*
 * This file created on Apr 14, 2005 by Rick Li
 */

/**
 * Improves query results by converting plural words to their singular
 * forms.
 *
 * @author Rick Li
 */
public class PluralFoldingFilter extends TokenFilter 
{
  /** Set of words to de-pluralize */
  private WordMap pluralMap;

  /**
   * Construct a token stream to convert plural words to singular.
   *
   * @param input       Input stream of tokens to process
   * @param pluralMap   Mapping of plural words to their singular equivalents.
   */
  public PluralFoldingFilter(TokenStream input, WordMap pluralMap) 
  {
    // Initialize the super-class
    super(input);

    // Record the set of words to de-pluralize
    this.pluralMap = pluralMap;
  } // constructor

  /** Retrieve the next token in the stream. */
  public Token next()
    throws IOException 
  {
    // Get the next token. If we're at the end of the stream, get out.
    Token t = input.next();
    if (t == null)
      return t;

    // Strip start-of-field and end-of-field markers (but remember that we did so.)
    String term = t.termText();

    boolean isStartTerm = false;
    boolean isEndTerm = false;

    if (term.length() > 0 && term.charAt(0) == Constants.FIELD_START_MARKER) {
      isStartTerm = true;
      term = term.substring(1);
    }

    if (term.length() > 0 &&
        term.charAt(term.length() - 1) == Constants.FIELD_END_MARKER) 
    {
      isEndTerm = true;
      term = term.substring(0, term.length() - 1);
    }

    // Is it a plural word? If not, return it unchanged.
    String mapped = pluralMap.lookup(term);
    if (mapped == null)
      return t;

    // Add the markers back in
    if (isStartTerm)
      mapped = Constants.FIELD_START_MARKER + mapped;
    if (isEndTerm)
      mapped = mapped + Constants.FIELD_END_MARKER;

    // Okay, we gotta make a new token that's the same in every respect
    // except the word.
    //
    Token newToken = new Token(mapped, t.startOffset(), t.endOffset(), t.type());
    newToken.setPositionIncrement(t.getPositionIncrement());
    return newToken;
  } // next()
} // class PluralFoldingFilter

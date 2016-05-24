package net.dataninja.ee.textIndexer;


/*
net.dataninja copyright statement
 */
import java.io.IOException;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import net.dataninja.ee.textEngine.Constants;

/**
 * Ensures that the tokens at the start and end of the stream are indexed both
 * with and without the special start-of-field/end-of-field markers.
 *
 * @author Rick Li
 */
public class StartEndFilter extends TokenFilter 
{
  /** Token queued for next() */
  private Token queuedToken = null;

  /**
   * Construct a token stream that fixes the start/end markers.
   *
   * @param input       Input stream of tokens to process
   */
  public StartEndFilter(TokenStream input) 
  {
    // Initialize the super-class
    super(input);
  } // constructor

  /** Retrieve the next token in the stream. */
  public Token next()
    throws IOException 
  {
    Token t;

    // If we have a token queued up, return that.
    if (queuedToken != null) {
      t = queuedToken;
      queuedToken = null;
      return t;
    }

    // Get the next token. If we're at the end of the stream, get out.
    t = input.next();
    if (t == null)
      return t;

    // If it starts or ends with the special token character, index both with and
    // without it.
    //
    String term = t.termText();
    boolean isStartToken = (term.charAt(0) == Constants.FIELD_START_MARKER);
    boolean isEndToken = (term.charAt(term.length() - 1) == Constants.FIELD_END_MARKER);
    if (isStartToken || isEndToken) {
      if (isStartToken)
        term = term.substring(1);
      if (isEndToken)
        term = term.substring(0, term.length() - 1);
      queuedToken = new Token(term, t.startOffset(), t.endOffset(), t.type());
      queuedToken.setPositionIncrement(0);
    }

    // Return the original token first.
    return t;
  } // next()
} // class StartEndFilter

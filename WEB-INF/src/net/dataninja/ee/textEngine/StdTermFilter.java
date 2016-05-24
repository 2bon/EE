package net.dataninja.ee.textEngine;


/*
dataninja copyright statement
 */
import java.io.IOException;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardFilter;
import net.dataninja.ee.util.FastStringReader;
import net.dataninja.ee.util.FastTokenizer;

/*
 * This file created on Jan 17, 2007 by Rick Li
 */

/**
 * Performs standard tokenization activities for terms, such as
 * mapping to lowercase, removing apostrophes, etc.
 *
 * @author Rick Li
 */
public class StdTermFilter 
{
  private DribbleStream dribble;
  private TokenStream filter;

  /**
   * During tokenization, the '*' wildcard has to be changed to a word
   * to keep it from being removed.
   */
  private static final String SAVE_WILD_STAR = "jwxbkn";

  /**
   * During tokenization, the '?' wildcard has to be changed to a word
   * to keep it from being removed.
   */
  private static final String SAVE_WILD_QMARK = "vkyqxw";

  /** Construct the rewriter */
  public StdTermFilter() {
    dribble = new DribbleStream();
    filter = new StandardFilter(new LowerCaseFilter(dribble));
  }

  /**
   * Apply the standard mapping to the given term.
   *
   * @return changed version, or original term if no change required.
   */
  public String filter(String term) 
  {
    dribble.nextToken = saveWildcards(term);
    try {
      Token mapped = filter.next();
      String restored = restoreWildcards(mapped.termText());
      if (restored.equals(term))
        return term;
      return restored;
    }
    catch (IOException e) {
      throw new RuntimeException("Very unexpected IO exception: " + e);
    }
  }

  /**
   * Converts wildcard characters into word-looking bits that would never
   * occur in real text, so the standard tokenizer will keep them part of
   * words. Resurrect using {@link #restoreWildcards(String)}.
   */
  protected static String saveWildcards(String s) 
  {
    // Early out if no wildcards found.
    if (s.indexOf('*') < 0 && s.indexOf('?') < 0)
      return s;

    // Convert to wordish stuff.
    s = s.replaceAll("\\*", SAVE_WILD_STAR);
    s = s.replaceAll("\\?", SAVE_WILD_QMARK);
    return s;
  } // saveWildcards()

  /**
   * Restores wildcards saved by {@link #saveWildcards(String)}.
   */
  protected static String restoreWildcards(String s) 
  {
    // Early out if no wildcards found.
    if (s.indexOf(SAVE_WILD_STAR) < 0 && s.indexOf(SAVE_WILD_QMARK) < 0)
      return s;

    // Convert back from wordish stuff to real wildcards.
    s = s.replaceAll(SAVE_WILD_STAR, "*");
    s = s.replaceAll(SAVE_WILD_QMARK, "?");
    return s;
  } // restoreWildcards()

  private class DribbleStream extends TokenStream 
  {
    public String nextToken;

    /** Return a token equal to the last one we were sent. */
    @Override
    public Token next()
      throws IOException 
    {
      FastTokenizer toks = new FastTokenizer(new FastStringReader(nextToken));
      Token t = toks.next();
      
      // If it doesn't see it as a token, make our own.
      if (t == null)
        return new Token(nextToken, 0, nextToken.length());
      
      // If the entire text wasn't consumed, ignore the result and make our
      // own token.
      //
      else if (t.startOffset() != 0 || t.endOffset() != nextToken.length()) 
        return new Token(nextToken, 0, nextToken.length());
      
      // Good, it consumed the whole thing. Return the parsed token.
      else
        return t;
    }
  }
} // class

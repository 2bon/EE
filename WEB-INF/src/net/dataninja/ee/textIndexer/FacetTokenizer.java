package net.dataninja.ee.textIndexer;


/*
dataninja copyright statement
 */
import java.io.IOException;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;

/*
 * This file created on Apr 14, 2005 by Rick Li
 */

/**
 * Performs special tokenization for facet fields. Looks for the hierarchy
 * marker "::" between hierarchy levels. For instance, the string
 * "US::California::Alameda County::Berkeley" would be made into four tokens:
 *
 *    US
 *    US::California
 *    US::California::Alameda County
 *    US::California::Alameda County::Berkeley
 *
 * @author Rick Li
 */
public class FacetTokenizer extends TokenStream 
{
  String str;
  int pos = 0;
  Token nextToken = null;

  /**
   * Construct a token stream to remove accents from the input tokens.
   *
   * @param str   The string to tokenize
   */
  public FacetTokenizer(String str) {
    this.str = str;
  } // constructor

  /** Retrieve the next token in the stream. */
  public Token next()
    throws IOException 
  {
    Token t;

    // Do we have a queued token? If so, return it.
    if (nextToken != null) 
    {
      t = nextToken;
      nextToken = null;
    }
    else {
      // Are we at the end? If so, tell the caller.
      if (pos > str.length())
        return null;

      // Find the next divider. If not found, eat everything to the end.
      pos = str.indexOf("::", pos);
      if (pos < 0)
        pos = str.length();

      // Grab the string, and un-do the temporary escaping of XML-special
      // characters.
      //
      String term = str.substring(0, pos);
      term = term.replace("&lt;", "<");
      term = term.replace("&gt;", ">");
      term = term.replace("&amp;", "&");
      
      // Form the new token and advance.
      t = new Token(term, 0, pos);
      pos += 2;

      // If the lower-case version is different, queue that token as well.
      // That way queries will work properly.
      //
      String lcTerm = term.toLowerCase();
      if (!lcTerm.equals(term))
        nextToken = new Token(lcTerm, 0, pos);
    }
    return t;
  } // next()
} // class FacetTokenizer

package net.dataninja.ee.util;


/**
net.dataninja copyright statement
 */
import java.io.IOException;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardTokenizerConstants;

/**
 * Used for debugging optimized FastTokenStream, this class checks the main
 * TokenStream against a reference one for equality. Any difference is flagged
 * with an assertion failure.
 *
 * @author Rick Li
 */
public class CheckingTokenStream extends TokenStream 
{
  /** Main token stream that is being checked */
  TokenStream main;

  /** Reference stream to check the main one against */
  TokenStream ref;

  /** Token type for words containing apostrophes */
  private static final String APOSTROPHE_TYPE = StandardTokenizerConstants.tokenImage[StandardTokenizerConstants.APOSTROPHE];

  /** Token type for acronyms */
  private static final String ACRONYM_TYPE = StandardTokenizerConstants.tokenImage[StandardTokenizerConstants.ACRONYM];

  /** Construct a CheckingTokenStream */
  public CheckingTokenStream(TokenStream main, TokenStream ref) 
  {
    this.main = main;
    this.ref = ref;

    // Assertions must be enabled!
    boolean flag = false;
    assert (flag = true) == true;
    if (!flag)
      throw new RuntimeException(
        "CheckingTokenStream requires assertions to be enabled");
  } // constructor

  /**
   * Get the next token from the main stream. Checks that this token matches
   * the next one in the reference stream.
   */
  public Token next()
    throws IOException 
  {
    Token t1 = main.next();
    Token t2 = ref.next();
    if (t1 == null || t2 == null)
      assert t1 == t2;
    else {
      assert t1.termText().equals(t2.termText());
      assert t1.startOffset() == t2.startOffset();
      assert t1.endOffset() == t2.endOffset();
      assert t1.getPositionIncrement() == t2.getPositionIncrement();
      assert idType(t1.type()).equals(idType(t2.type()));
    }
    return t1;
  }

  /** Map the type to apostrophe, acronym, or other */
  private String idType(String type) {
    if (type.equals(APOSTROPHE_TYPE))
      return APOSTROPHE_TYPE;
    if (type.equals(ACRONYM_TYPE))
      return ACRONYM_TYPE;
    return "other";
  }

  /** Close the token stream */
  public void close()
    throws IOException 
  {
    main.close();
    ref.close();
  }
} // class CheckingTokenizer

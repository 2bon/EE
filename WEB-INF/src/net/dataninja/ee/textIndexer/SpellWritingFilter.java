package net.dataninja.ee.textIndexer;


/*
net.dataninja copyright statement
 *
 * Acknowledgements:
 *
 * A significant amount of new and/or modified code in this module
 * was made possible by a grant from the Andrew W. Mellon Foundation,
 * as part of the Melvyl Recommender Project.
 */
import java.io.IOException;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.spelt.SpellWriter;
import net.dataninja.ee.textEngine.Constants;

/**
 * Adds words from the token stream to a SpellWriter.
 *
 * @author Rick Li
 */
public class SpellWritingFilter extends TokenFilter 
{
  /** Spelling writer to write to */
  private SpellWriter writer;

  /** true before the first token is returned */
  private boolean firstTime = true;

  /**
   * Construct a token stream to add tokens to a spelling correction
   * dictionary.
   *
   * @param input       Input stream of tokens to process
   * @param writer      Spelling dictionary writer
   */
  public SpellWritingFilter(TokenStream input, SpellWriter writer) 
  {
    // Initialize the super-class
    super(input);

    this.writer = writer;
  } // constructor

  /** Retrieve the next token in the stream. */
  public Token next()
    throws IOException 
  {
    // Get the next token. If we're at the end of the stream, get out.
    Token t = input.next();
    if (t == null)
      return t;

    // Make sure the first token for this field doesn't get paired with a
    // token from the previous field.
    //
    if (firstTime) {
      writer.queueBreak();
      firstTime = false;
    }

    // Skip words with start/end markers
    String word = t.termText();
    boolean skip = false;
    if (word.charAt(0) == Constants.FIELD_START_MARKER)
      skip = true;
    else if (word.charAt(word.length() - 1) == Constants.FIELD_END_MARKER)
      skip = true;

    // Skip words with digits. We seldom want to correct with these,
    // and they introduce a big burden on indexing. Also, skip element
    // and attribute markers.
    //
    else 
    {
      for (int i = 0; i < word.length(); i++) 
      {
        char c = word.charAt(i);
        if (Character.isDigit(c) ||
            c == Constants.ELEMENT_MARKER ||
            c == Constants.ATTRIBUTE_MARKER) 
        {
          skip = true;
          break;
        }
      }
    }

    // If we're not skipping the word, queue it.
    if (!skip) 
    {
      // Don't record pairs across sentence boundaries
      if (t.getPositionIncrement() != 1)
        writer.queueBreak();

      // Queue the word
      writer.queueWord(word);
    }
    else
      writer.queueBreak();

    // Pass on the token unchanged.
    return t;
  } // next()
} // class AccentFoldingFilter

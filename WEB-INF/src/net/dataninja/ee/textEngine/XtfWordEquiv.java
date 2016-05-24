package net.dataninja.ee.textEngine;


/**
dataninja copyright statement
 */
import org.apache.lucene.spelt.WordEquiv;
import net.dataninja.ee.util.CharMap;
import net.dataninja.ee.util.FastCache;
import net.dataninja.ee.util.WordMap;

/** Used for eliminating redundant spelling suggestions */
public class XtfWordEquiv implements WordEquiv 
{
  private CharMap accentMap;
  private WordMap pluralMap;
  private StdTermFilter stdTermFilter = new StdTermFilter();
  private FastCache recent = new FastCache(1000);

  public XtfWordEquiv(CharMap accentMap, WordMap pluralMap) {
    this.accentMap = accentMap;
    this.pluralMap = pluralMap;
  }

  /**
   * Checks if two words can be considered equivalent, and thus not form a
   * real spelling suggestion.
   */
  public boolean isEquivalent(String word1, String word2) 
  {
    // Filter both words (convert to lower case, remove plurals, etc.)
    word1 = filter(word1);
    word2 = filter(word2);

    // And compare the filtered versions.
    return word1.equals(word2);
  }

  private String filter(String in) 
  {
    String out = (String)recent.get(in);
    if (out == null) 
    {
      out = stdTermFilter.filter(in);

      // Next, ignore accents.
      String tmp;
      if (accentMap != null) {
        tmp = accentMap.mapWord(out);
        if (tmp != null)
          out = tmp;
      }

      // Then ignore plurals.
      if (pluralMap != null) {
        tmp = pluralMap.lookup(out);
        if (tmp != null)
          out = tmp;
        recent.put(in, out);
      }
    }
    return out;
  }
}

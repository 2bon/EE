package net.dataninja.ee.textEngine;

import java.util.Set;
import net.dataninja.ee.util.CharMap;
import net.dataninja.ee.util.WordMap;

/*
net.dataninja copyright statement
 */

/**
 * Tracks the context in which a query was executed. This includes the list
 * of stop words applied, and the plural and accent maps used.
 *
 * @author Rick Li
 */
public class QueryContext 
{
  /** The set of stopwords used when processing the query. */
  public Set stopSet;

  /** The plural map used when processing the query. */
  public WordMap pluralMap;

  /** The accent map used when processing the query. */
  public CharMap accentMap;
} // class QueryContext

package net.dataninja.ee.textEngine;

import java.io.IOException;

/*
net.dataninja copyright statement
 */

/*
 * This file created on Mar 1, 2005 by Rick Li
 */

/**
 * Takes a QueryRequest, rewrites the queries if necessary to remove stop-
 * words and form bi-grams, then consults the index(es), and produces a
 * QueryResult.
 *
 * @author Rick Li
 */
public abstract class QueryProcessor 
{
  /**
   * Takes a query request and handles searching the index and forming
   * the results.
   *
   * @param req   The request to process
   * @return      Zero or more document hits
   */
  public abstract QueryResult processRequest(QueryRequest req)
    throws IOException;

  /**
   * Optional method: hint to the query processor to clear any cached
   * index data, so that recently indexed documents will appear in
   * search results.
   */
  public void resetCache() {
  }
  
  /**
   * Optional method: set the ee home directory (used for background
   * warming in the default query processor.)
   */
  public void setXtfHome(String homeDir) {
  }

  /**
   * Optional method: set the background warmer for indexes.
   */
  public void setIndexWarmer(IndexWarmer warmer) {
  }
}

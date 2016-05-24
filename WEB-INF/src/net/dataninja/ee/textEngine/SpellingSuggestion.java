package net.dataninja.ee.textEngine;


/*
dataninja copyright statement
 */

/**
 * Contains one or more suggestions for a specific term in a query.
 */
public class SpellingSuggestion 
{
  /** The original term from the query */
  public String origTerm;

  /** List of fields in which the original term was queried */
  public String[] fields;

  /**
   * The suggested alternative term... might be null if original term
   * should be deleted.
   */
  public String suggestedTerm;
} // class SpellingSuggestion

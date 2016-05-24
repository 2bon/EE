package net.dataninja.ee.textEngine;


/*
dataninja copyright statement
 */

/**
 * Various parameters for applying a boost set to query results.
 */
public class BoostSetParams 
{
  /** Path of file containing document keys -> boost factors. */
  public String path;

  /** Field name for boost set document keys. */
  public String field;

  /** Exponent applied to all boost set values. */
  public float exponent = 1.0f;

  /** Default value if document not in boost set. */
  public float defaultBoost = 1.0f;
} // class SpellcheckParams

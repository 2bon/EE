package net.dataninja.ee.textEngine;


/**
net.dataninja copyright statement
 */

/**
 * Holds all the information regarding a specific text snippet within a
 * document.
 *
 * @author Rick Li
 */
public class Snippet 
{
  /** Rank of this snippet in the document, zero being the "best" */
  public int rank;

  /** Score of the snippet */
  public float score;

  /**
   * Text of the snippet, including the hit and as much context as
   * poss
   */
  public String text;

  /** 'sectionType' from the original document, if any */
  public String sectionType;

  /** Node number of the first word in the hit */
  public int startNode = -1;

  /** Word number of the first word in the hit */
  public int startOffset = -1;

  /** Node number of the last word in the hit */
  public int endNode = -1;

  /** Word number of the last word in the hit, plus one */
  public int endOffset = -1;
} // class Snippet

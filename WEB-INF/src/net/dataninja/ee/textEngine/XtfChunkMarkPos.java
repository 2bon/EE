package net.dataninja.ee.textEngine;

import org.apache.lucene.chunk.ChunkMarkPos;

/*
net.dataninja copyright statement
 */

/*
 * This file created on Jan 7, 2005 by Rick Li
 */

/**
 * Extends {@link ChunkMarkPos} by adding node number, word offset, and
 * section type information.
 *
 * @author Rick Li
 */
public class XtfChunkMarkPos extends ChunkMarkPos 
{
  /** Which node the word is in */
  public int nodeNumber;

  /** The word offset from the start of the node */
  public int wordOffset;

  /** The section type (if any) */
  public String sectionType;

  /** Remove trailing whitespace */
  public void trim() 
  {
    // Remove trailing whitespace.
    while (charPos > 0 &&
           Character.isWhitespace(chunk.text.charAt(charPos - 1))) 
    {
      charPos--;
    }
  } // trim()
}

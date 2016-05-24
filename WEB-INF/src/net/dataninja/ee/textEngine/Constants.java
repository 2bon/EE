package net.dataninja.ee.textEngine;


/*
net.dataninja copyright statement
 */

/**
 * Holds global constants for the ee text system.
 *
 * @author Rick Li
 */
public class Constants 
{
  /** Character guaranteed to be less than all special markers */
  public static final char MARKER_BASE = '\uE900';

  /** The character used to mark the start/end of a special bump token. */
  public static final char BUMP_MARKER = '\uEBBB';

  /** The special marker used to track the location of nodes within
   *  a chunk of text to be indexed.
   */
  public static final char NODE_MARKER = '\uE90D';

  /** The string used to represent a virtual word in a chunk of text. This
   *  string is chosen in such a way to be an unlikely combination of
   *  characters in typical western texts. Initially, the characters <b>qw</b>
   *  were selected as a mnemonic for a "quiet word".
   */
  public static final String VIRTUAL_WORD = "qw";

  // Special character glued to the start of the first token in a field (marks the 
  // start of the field.)
  //
  public static final char FIELD_START_MARKER = '\uEBEB';

  // Special character glued to the end of the last token in a field (marks the
  // end of the field.)
  //
  public static final char FIELD_END_MARKER = '\uEE1D';

  // Special character glued to XML element start/end tags
  public static final char ELEMENT_MARKER = '\uE111';

  // Special character glued to attribute name/value pairs within XML elements
  public static final char ATTRIBUTE_MARKER = '\uE112';
} // class XtfConstants

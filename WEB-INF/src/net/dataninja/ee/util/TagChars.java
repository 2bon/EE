package net.dataninja.ee.util;


/*
dataninja copyright statement
 */

/**
 * Class that represents a slice of a block, for quick access to byte-level
 * strings without object allocation.
 */
@SuppressWarnings("cast")
public class TagChars {
  int length;
  byte[] block;
  int offset;

  public final char charAt(int index) {
    return (char)((int)block[offset + index] & 0xff);
  }

  public final int length() {
    return length;
  }

  public final int indexOf(char c) 
  {
    for (int i = 0; i < length; i++) {
      if (block[offset + i] == c)
        return i;
    }
    return -1;
  }

  /** Determines how many characters match at the start of two sequences */
  public final int prefixMatch(TagChars other) 
  {
    int minLength = Math.min(length, other.length);
    int i;
    for (i = 0; i < minLength; i++) {
      if (block[offset + i] != other.block[other.offset + i])
        break;
    }
    return i;
  }

  public final String toString() {
    char[] chars = new char[length];
    for (int i = 0; i < length; i++)
      chars[i] = charAt(i);
    return new String(chars);
  }
}

package net.dataninja.ee.util;


/**
net.dataninja copyright statement
 */

/**
 * Utility class that decodes Base64 data.
 */
public class Base64 
{
  /**
   * Given a character in the Base64 set, figure out the decimal
   * equivalent.
   *
   * @param c     The character to decode
   * @return      The decimal equivalent (also maps invalid chars to zero)
   */
  private static int decodeChar(char c) {
    if (c >= 'A' && c <= 'Z')
      return c - 'A';
    else if (c >= 'a' && c <= 'z')
      return c - 'a' + 26;
    else if (c >= '0' && c <= '9')
      return c - '0' + 52;
    else if (c == '+')
      return 62;
    else if (c == '/')
      return 63;
    else
      return 0;
  } // decodeChar()

  /**
   * Combines bits from two different bytes into a single character.
   */
  private static String decodeBits(int bits1, int pos1, int count1, int bits2,
                                   int pos2, int count2) 
  {
    int num1 = (bits1 >> pos1) & (0xFF >> (8 - count1));
    int num2 = (bits2 >> pos2) & (0xFF >> (8 - count2));
    char c = (char)((num1 << count2) + num2);
    return Character.toString(c);
  } // decodeBits()

  /**
   * Decodes a 4-character Base64 'quantum' into a 3-character string.
   */
  private static String decodeQuantum(String quantum) 
  {
    int[] bits = new int[4];
    for (int i = 0; i < 4; i++)
      bits[i] = decodeChar(quantum.charAt(i));

    String ch1 = decodeBits(bits[0], 0, 6, bits[1], 4, 2);
    String ch2 = decodeBits(bits[1], 0, 4, bits[2], 2, 4);
    String ch3 = decodeBits(bits[2], 0, 2, bits[3], 0, 6);
    if (quantum.endsWith("=="))
      return ch1;
    else if (quantum.endsWith("="))
      return ch1 + ch2;
    else
      return ch1 + ch2 + ch3;
  } // decodeQuantum()

  /**
   * Decodes a full Base64 string to the corresponding normal string.
   *
   * @param base64    The base64 string to decode (e.g. "HX1+9/6fE97=")
   * @return          Decoded version of the string.
   */
  public static String decodeString(String base64) 
  {
    String out = "";
    while (base64.length() >= 4) {
      String quantum = base64.substring(0, 4);
      base64 = base64.substring(4);
      String str = decodeQuantum(quantum);
      out += str;
    }

    return out;
  } // decodeString()
} // class Base64

package net.dataninja.ee.xslt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

import net.dataninja.ee.util.CharMap;

import net.sf.saxon.expr.XPathContext;

/*
dataninja copyright statement
 */

/*
 * This file created on Jun 4, 2009 by Rick Li
 */

/**
 * Provides Unicode character-related utilities to be called by XSLT 
 * stylesheets through Saxon's extension function mechanism.
 *
 * @author Rick Li
 */
public class CharUtils 
{
  /** Used to avoid recreating AccentMap objects all the time */
  private static HashMap<String,CharMap> accentMapCache = new HashMap();

  /** 
   * Get the accent map corresponding to a file. 
   * 
   * @throws IOException if we can't read the file.
   */
  private static CharMap getAccentMap(XPathContext context, String filePath) 
    throws IOException
  {
    synchronized (accentMapCache) 
    {
      // First resolve relative path name.
      String fullPath = FileUtils.resolvePath(context, filePath);
      File file = new File(fullPath);
      
      // Make sure we can read the file.
      if (!file.canRead())
        throw new IOException("Error reading accent map file '" + fullPath + "'");
      
      // Do we already have this version of this file loaded?
      String key = fullPath + "|" + file.lastModified();
      if (accentMapCache.containsKey(key))
        return accentMapCache.get(key);
      
      // Okay, we need to read and cache it.
      InputStream stream = new FileInputStream(file);
      if (fullPath.endsWith(".gz"))
        stream = new GZIPInputStream(stream);
      CharMap map = new CharMap(stream);
      accentMapCache.put(key, map);
      return map;
    }
  }
  
  /**
   * Applies an accent map to a string, normalizing spaces in the process.
   * This function is typically used to remove diacritic marks from 
   * alphabetic characters. The accent map is read
   * from the file with the given path. If the path is relative, it is 
   * resolved relative to the stylesheet calling this function. Note that
   * the accent map is cached in memory so it doesn't need to be
   * repeatedly read.
   *
   * @param context   Context used to figure out which stylesheet is calling
   *                  the function.
   * @param filePath  Path to the accent map file in question (typically
   *                  leading to conf/accentFolding/accentMap.txt)
   * @param str       The string whose characters should be mapped.
   * @return          A new string with its characters mapped.
   */
  public static String applyAccentMap(XPathContext context, 
                                      String filePath,
                                      String str) 
    throws IOException
  {
    // First read in (or get cached) accent map file.
    CharMap accentMap;
    accentMap = getAccentMap(context, filePath);
    
    // Then apply it to each word.
    StringBuilder buf = new StringBuilder();
    for (String word : str.split("\\s")) {
      if (word.length() == 0)
        continue;
      String mappedWord = accentMap.mapWord(word);
      if (mappedWord != null)
        word = mappedWord;
      if (buf.length() > 0)
        buf.append(' ');
      buf.append(word);
    }
    
    // Return the result joined by spaces.
    return buf.toString();
  }
  
} // class CharUtils

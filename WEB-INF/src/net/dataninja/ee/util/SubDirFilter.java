package net.dataninja.ee.util;


/**
dataninja copyright statement
 */

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////

/**
 * This class provides an efficient means to determine if a given subdirectory
 * is "in" or "out" of the set of directories specified to index. Essentially,
 * if a given directory has an ancestor or a descendant in the set, it
 * qualifies. That is, ancestors and cousins of the set directories will be
 * indexed, but not necessarily all the cousins, nephews, nieces, etc.
 */
public class SubDirFilter
{
  private HashSet<String> targets   = new HashSet();
  private HashSet<String> ancestors = new HashSet();

  /** Tell if nothing has been added yet */
  public boolean isEmpty() {
    return targets.isEmpty();
  }

  /**
   * Adds a directory to the set.
   */
  public void add(File dirFile) {
    targets.add(dirFile.toString());
    for (String a : ancestorOrSelf(dirFile)) {
      if (!ancestors.add(a))
        break;
    }
  }
  
  /**
   * Returns the number of targets that have been added to the filter.
   */
  public int size()
  {
    return targets.size();
  }

  /**
   * Get a list of all targets in lexicographic order.
   */
  public List<String> getTargets() 
  {
    ArrayList<String> list = new ArrayList(targets);
    Collections.sort(list);
    return list;
  }
  
  /**
   * Checks if the given directory is in the set, where "in" is defined as
   * having an ancestor or descendant within the set.
   */
  public boolean approve(String dir) {
    return approve(new File(Path.normalizePath(dir)));
  }
  
  /**
   * Checks if the given directory is in the set, where "in" is defined as
   * having an ancestor or descendant within the set.
   */
  public boolean approve(File dirFile)
  {
    // If this dir has descendants in the set, yes.
    if (ancestors.contains(dirFile.toString()))
      return true;
    
    // If this dir has ancestors in the set, yes.
    for (String a : ancestorOrSelf(dirFile)) {
      if (targets.contains(a))
        return true;
    }
    
    // Otherwise, no.
    return false;
  }

  /**
   * Make a list of the directory and all its ancestors.
   */
  private ArrayList<String> ancestorOrSelf(File dir)
  {
    ArrayList<String> list = new ArrayList();
    boolean found = false;
    for (; !found && dir != null; dir = dir.getParentFile())
      list.add(dir.toString());
    return list;
  }
  
} // class SubdirFilter

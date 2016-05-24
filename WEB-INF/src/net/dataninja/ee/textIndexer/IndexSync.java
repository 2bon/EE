package net.dataninja.ee.textIndexer;


/**
dataninja copyright statement
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.dataninja.ee.util.DirSync;
import net.dataninja.ee.util.SubDirFilter;
import net.dataninja.ee.util.Trace;

/**
 * Takes care of copying the differences between a source index and a dest
 * index to make them exactly equal. Doesn't have to scan every data directory
 * and lazy file, since it uses the DocSelCache to get an idea of the subset
 * of things that actually need to be scanned.
 */
public class IndexSync 
{
  private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S Z");
  
  /**
   * Perform the minimum necessary work to ensure that the contents of dstDir
   * exactly match srcDir.
   * @param indexName 
   * 
   * @throws IOException        If anything goes wrong.
   */
  public void syncDirs(String indexName, File srcDir, File dstDir) throws IOException
  {
    SubDirFilter filter = calcFilter(indexName, srcDir, dstDir);
    DirSync dirSync = new DirSync(filter);
    dirSync.syncDirs(srcDir, dstDir);
  }
  
  /**
   * Determine the sub-directory filter for directory scanning. If the dst
   * is an ancestor of src, we do intelligent filtering; otherwise we scan
   * the whole thing.
   * @throws IOException If anything goes wrong.
   */
  private SubDirFilter calcFilter(String indexName, File srcDir, File dstDir) 
    throws IOException 
  {
    SubDirFilter filter = new SubDirFilter();
    
    String srcTime = oldestTime(srcDir);
    String dstTime = oldestTime(dstDir);
    
    File srcScanFile = new File(srcDir, "scanDirs.list");
    File dstScanFile = new File(dstDir, "scanDirs.list");
    
    if (srcTime.equals(dstTime) &&
        srcScanFile.canRead() &&
        dstScanFile.canRead())
    {
      File srcLazyDir = null;
      File srcCloneDir = null;
      for (File f : srcDir.listFiles())
      {
        if (!f.isDirectory())
          continue;
        else if (f.getName().equals("lazy"))
          srcLazyDir = f;
        else if (f.getName().equals("dataClone"))
          srcCloneDir = f;
        else
          filter.add(f);
      }
      
      BufferedReader srcScanReader = new BufferedReader(new FileReader(srcScanFile));
      BufferedReader dstScanReader = new BufferedReader(new FileReader(dstScanFile));
      
      // Verify that the files are identical up to the point where dst ends.
      boolean allMatch = true;
      while (true)
      {
        String dstKey = dstScanReader.readLine();
        if (dstKey == null)
          break;
        String srcKey = srcScanReader.readLine();
        if (!dstKey.equals(srcKey))
          allMatch = false;
      }

      // If they match, we need only scan the directories in src that came later.
      if (allMatch)
      {
        while (true)
        {
          String srcKey = srcScanReader.readLine();
          if (srcKey == null)
            break;
          String scanDir = srcKey.replaceFirst(":", "/");
          if (srcLazyDir != null)
            filter.add(new File(srcLazyDir, scanDir));
          if (srcCloneDir != null)
            filter.add(new File(srcCloneDir, scanDir));
          
        }
      }
    }
    
    if (filter.isEmpty() || filter.size() > DirSync.MAX_SELECTIVE_SYNC) {
      Trace.info("Syncing entire source directory.");
      filter.add(srcDir);
    }
    else
      Trace.info("Syncing changed directories only.");
    
    return filter;
  }

  /** 
   * Determine the oldest file within a directory (or the dir itself if empty) and
   * return a human-readable version of that time.
   */
  public static String oldestTime(File dir)
  {
    long min = Long.MAX_VALUE;
    for (File f : dir.listFiles()) {
      if (f.getName().equals("scanDirs.list"))
        continue;
      if (f.lastModified() < min)
        min = f.lastModified();
    }
    if (min == Long.MAX_VALUE)
      min = dir.lastModified();
    return dateFormat.format(new Date(min));
  }

  /** 
   * Determine the newest file within a directory (or the dir itself if empty) and
   * return a human-readable version of that time.
   */
  public static String newestTime(File dir)
  {
    long max = Long.MIN_VALUE;
    for (File f : dir.listFiles()) {
      if (f.getName().equals("scanDirs.list"))
        continue;
      if (f.lastModified() > max)
        max = f.lastModified();
    }
    if (max == Long.MIN_VALUE)
      max = dir.lastModified();
    return dateFormat.format(new Date(max));
  }
}

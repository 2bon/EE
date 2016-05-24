package net.dataninja.ee.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.dataninja.ee.util.ProcessRunner.CommandFailedException;

/**
dataninja copyright statement
 */

/**
 * Routines to synchronize one directory hierarchy to match another. Now uses
 * rsync for speed and simplicity, and adds a threshold above which we avoid
 * per-subdirectory syncing and just do the whole thing.
 *
 * @author Rick Li
 */
public class DirSync 
{
  public static final int MAX_SELECTIVE_SYNC = 500;
  private static final int MAX_RSYNC_BATCH = 2;
  private SubDirFilter filter;

  /**
   * Initialize a directory syncer with no sub-directory filter
   * (all sub-directories will be scanned.)
   */
  public DirSync() { 
    this(null);
  }
  
  /**
   * Initialize with a sub-directory filter.
   */
  public DirSync(SubDirFilter filter) {
    this.filter = filter;
  }
  
  
  /**
   * Sync the files from source to dest.
   * 
   * @param srcDir      Directory to match
   * @param dstDir      Directory to modify
   * @throws IOException If anything goes wrong
   */
  public void syncDirs(File srcDir, File dstDir) 
    throws IOException
  {
    // If there are no directories specified, or there are too many, or if only
    // the top-level directory is being sync'd, just rsync the entire source to 
    // the dest.
    //
    if (filter == null || filter.size() > MAX_SELECTIVE_SYNC ||
        (filter.size() == 1 && 
         new File(filter.getTargets().get(0)).getCanonicalFile().equals(srcDir.getCanonicalFile())))
    {
      runRsync(srcDir, dstDir, null, new String[] { "--exclude=scanDirs.list" });
    }
    
    // Otherwise do a selective sync.
    else
      selectiveSync(srcDir, dstDir);
    
    // Always do the scanDirs.list file last, since it governs incremental syncing.
    // If it were done before other files, and the sync process aborted, we might
    // mistakenly think two directories were perfectly in sync when in fact they
    // are different.
    //
    runRsync(new File(srcDir, "scanDirs.list"), dstDir, null, null);
  }
  
  /**
   * The main workhorse of the scanner.
   * 
   * @param srcDir      Directory to match
   * @param dstDir      Directory to modify
   * @throws IOException If anything goes wrong
   */
  private void selectiveSync(File srcDir, File dstDir) 
    throws IOException
  {
    // First, sync the top-level files (no sub-dirs)
    runRsync(srcDir, dstDir, null, new String[] { "--exclude=/*/", "--exclude=scanDirs.list" });
    
    // Now sync the subdirectories in batches, not to exceed the batch limit
    if (!filter.isEmpty())
    {
      ArrayList<String> dirBatch = new ArrayList();
      String basePath = srcDir.getCanonicalPath() + "/";
      for (String target : filter.getTargets()) 
      {
        String targetPath = new File(target).getCanonicalPath();
        assert targetPath.startsWith(basePath) : ("targetPath '" + targetPath.toString() + "' should start with basePAth '" + basePath.toString() + "'");
        targetPath = targetPath.substring(basePath.length());
        
        dirBatch.add(targetPath);
        if (dirBatch.size() >= MAX_RSYNC_BATCH) {
          runRsync(srcDir, dstDir, dirBatch, null);
          dirBatch.clear();
        }
      }
      
      // Finish the last batch of subdirs (if any)
      if (!dirBatch.isEmpty())
        runRsync(srcDir, dstDir, dirBatch, new String[] { "--exclude=scanDirs.list" });
    }
  }
  
  /**
   * Run an rsync command with the standard arguments plus the
   * specified subdirectories and optional extra args.
   *
   * @param src          Directory (or file) to match
   * @param dst          Directory (or file) to modify
   * @param subDirs      Sub-directories to rsync (null for all)
   * @throws IOException If anything goes wrong
   */
  public void runRsync(File src, File dst, 
                       List<String> subDirs,
                       String[] extraArgs) 
    throws IOException
  {
    try 
    {
      // First the basic arguments
      ArrayList<String> args = new ArrayList(6);
      args.add("rsync");
      args.add("-av");
      //args.add("--dry-run");
      args.add("--delete");
      
      // Add any extra arguments at this point, before the paths.
      if (extraArgs != null) {
        for (String extra : extraArgs)
          args.add(extra);
      }

      // We want to hard link dest files to the source
      if (src.isDirectory())
        args.add("--link-dest=" + src.getAbsolutePath() + "/");
      
      // For the source, add in the weird "./" syntax for relative syncing, e.g.
      // rsync --relative server.org:data/13030/pairtree_root/qt/00/./{01/d5,04/k4} data/13030/pairtree_root/qt/00/
      //
      if (subDirs != null) { 
        args.add("--relative");
        for (String subDir : subDirs) 
        {
          if (new File(src.getAbsolutePath(), subDir).canRead())
            args.add(src.getAbsolutePath() + "/./" + subDir);
        }
      }
      else
        args.add(src.getAbsolutePath() + (src.isDirectory() ? "/" : ""));

      // Finally add the destination path
      args.add(dst.getAbsolutePath() + (dst.isDirectory() ? "/" : ""));

      // And run the command
      String[] argArray = args.toArray(new String[args.size()]);
      ProcessRunner.runAndGrab(argArray, "", 0);
    } 
    catch (InterruptedException e) {
      throw new RuntimeException(e);
    } 
    catch (CommandFailedException e) {
      throw new IOException(e.getMessage());
    }
  }
}

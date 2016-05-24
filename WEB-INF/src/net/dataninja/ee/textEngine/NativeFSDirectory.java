package net.dataninja.ee.textEngine;


/*
dataninja copyright statement
 */
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockFactory;
import org.apache.lucene.store.NativeFSLockFactory;

public class NativeFSDirectory extends FSDirectory
{
  private static HashMap<File, LockFactory> lockFactories = new HashMap();
  
  /** Returns the directory instance for the named location.
   * @param path the path to the directory.
   * @return the FSDirectory for the named file.  */
  public static FSDirectory getDirectory(String path)
    throws IOException 
  {
    return getDirectory(new File(path));
  }

  /** Returns the directory instance for the named location.
   * @param file the path to the directory.
   * @return the FSDirectory for the named file.  */
  public static FSDirectory getDirectory(File file)
    throws IOException 
  {
    file = new File(file.getCanonicalPath());
    return getDirectory(file, getLockFactory(file));
  }
  
  /** Get the lock factory for the given directory. If none yet, create one. */
  private static synchronized LockFactory getLockFactory(File f) 
    throws IOException 
  {
    LockFactory ret = lockFactories.get(f);
    if (ret == null) {
      ret = new NativeFSLockFactory(f);
      lockFactories.put(f, ret);
    }
    return ret;
  }
}
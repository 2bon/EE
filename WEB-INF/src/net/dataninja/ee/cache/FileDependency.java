package net.dataninja.ee.cache;


/**
net.dataninja copyright statement
 */
import java.io.File;

/**
 * This class represents a dependency on a given file. The dependency becomes
 * stale if the file modification time changes after the dependency is created.
 */
public class FileDependency extends Dependency 
{
  /**
   * Constructor - stores the modification date of the file.
   *
   * @param file  The file to base the dependency on.
   */
  public FileDependency(File file) {
    this.file = file;
    this.lastModified = file.lastModified();
  }

  /**
   * Constructor - stores the modification date of the file.
   *
   * @param path  Full path to the file on which to base the dependency.
   */
  public FileDependency(String path) {
    this(new File(path));
  }

  /**
   * Checks if this dependency is still valid.
   *
   * @return  true iff the file modification time is unchanged.
   */
  public boolean validate() 
  {
    // We don't have a good way at present to quickly check the last-mod
    // date of a URL. So skip it.
    //
    if (file.getPath().startsWith("http:"))
      return true;

    // If we can still read the file, check the mod time.
    return (file.canRead() && file.lastModified() == lastModified);
  }

  /** Make a human-readable representation */
  public String toString() {
    return "FileDependency(" + file.toString() + ":" + lastModified;
  }

  /** The file we're tracking */
  private File file;

  /** When the file was modified */
  private long lastModified;
} // class FileDependency
